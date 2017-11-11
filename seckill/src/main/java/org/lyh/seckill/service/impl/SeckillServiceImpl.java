package org.lyh.seckill.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.lyh.seckill.dao.SeckillDao;
import org.lyh.seckill.dao.SuccessKilledDao;
import org.lyh.seckill.dao.cache.RedisDao;
import org.lyh.seckill.dto.Exposer;
import org.lyh.seckill.dto.SeckillExecution;
import org.lyh.seckill.entity.Seckill;
import org.lyh.seckill.entity.SuccessKilled;
import org.lyh.seckill.enums.SeckillStateEnum;
import org.lyh.seckill.exception.RepeatKillException;
import org.lyh.seckill.exception.SeckillCloseException;
import org.lyh.seckill.exception.SeckillException;
import org.lyh.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
/*
 * 库存接口实现类
 * 
 */
@Service
public class SeckillServiceImpl implements SeckillService {
	private Logger logger=LoggerFactory.getLogger(this.getClass());
	@Autowired
	private RedisDao redisDao;
	@Autowired
	private SeckillDao seckillDao;
	@Autowired
	private SuccessKilledDao successKilledDao;
	//md5盐值字符串，用于混淆MD5，可以随便输，越无规则越乱越好
	private final String slat="sajriwqo^95u3899^$";	
	public List<Seckill> getSeckillList() {
		return seckillDao.queryAll(0, 4);
	}

	public Seckill getById(long seckillId) {
		return seckillDao.queryById(seckillId);
	}

	public Exposer exportSeckillUrl(long seckillId) {
		//缓存优化，优化暴露接口，
		Seckill seckill = redisDao.getSeckill(seckillId);
		if(seckill == null){
			 seckill = seckillDao.queryById(seckillId);
			 if(seckill == null){
					return new Exposer(false,seckillId);
			}else{
				redisDao.putSeckill(seckill);
			}
		}
		
		Date startTime=seckill.getStartTime();
		Date endTime=seckill.getEndTime();
		Date nowTime=new Date();//当前时间
//		Calendar calendar =new GregorianCalendar();
//		calendar.setTime(nowTime);
//		calendar.add(calendar.DATE, -1);
//		nowTime=calendar.getTime();		
		if(nowTime.getTime()<startTime.getTime()||nowTime.getTime()>endTime.getTime()){
			return new Exposer(false,seckillId,nowTime.getTime(),startTime.getTime(),endTime.getTime());
		}
		//转化特定字符串的过程，md5的最大特点是不可逆~
		String md5 = getMd5(seckillId);
		return new Exposer(true,md5,seckillId);
	}
	private String getMd5(long seckillId){
		String base = seckillId+"/"+slat;//这里就用到了上面声明的盐值，如果没有盐值还是有可能破解这个算法
		String md5 = DigestUtils.md5DigestAsHex(base.getBytes());//通过spring提供的工具类进行加密
		return md5;
	}
	/*
	 *使用注解控制事务方法的优点:
	 *1.开发团队可以形成约定，明确标准事务方法的编程风格（） 
	 *2.保证事务方法的执行时间尽可能短，不要穿插其他的网络操作 （如HTTP请求，
	 *实在需要这些方法就抽象出方法，剥离到事务外，让这些事务方法尽可能干净！（因为要做秒杀系统）
	 *3.不是所有的方法都需要事务，例如只读操作，只有一条修改操作等
	 */
	@Transactional
	public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
			throws SeckillException, SeckillCloseException, RepeatKillException {
		if(md5 == null || !(md5.equals(getMd5(seckillId)))){
			throw new SeckillException("用户的md5信息异常");
		}
		//执行秒杀逻辑：减库存+记录购买行为
		try {
			Date nowTime = new Date();
//			Calendar calendar =new GregorianCalendar();
//			calendar.setTime(nowTime);
//			calendar.add(calendar.DATE, -1);
//			nowTime=calendar.getTime();	
			
			int insertnum = successKilledDao.insertSuccessKilled(seckillId, userPhone);
			if(insertnum <= 0){
				//重复秒杀
				throw new RepeatKillException("重复秒杀了哦");
			}else{
				//更新库存，热点商品的竞争
				int updatenum=seckillDao.reduceNumber(seckillId, nowTime);
				if(updatenum <= 0){
					//秒杀结束,事务回滚
					throw new SeckillCloseException("秒杀结束了哦");
				}else{
					//秒杀成功,事务提交
					SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
					return new SeckillExecution(seckillId,SeckillStateEnum.SUCCESS,successKilled);
				}
			}
		}catch(SeckillCloseException e1){
			logger.error(e1.getMessage(),e1);
			throw new SeckillCloseException("秒杀结束了哦");
		}catch(RepeatKillException e2){
			logger.error(e2.getMessage(),e2);
			throw new RepeatKillException("重复秒杀了哦");
		}catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new SeckillException("秒杀逻辑内部异常");
		}
	}
	//通过存储过程去控制秒杀
	public SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5) {
		if(md5 == null || md5.equals(getMd5(seckillId))){
			return new SeckillExecution(seckillId,SeckillStateEnum.DATA_REWRITE);
		}
		Date killtime = new Date();
		Map<String,Object> map =new HashMap<String,Object>();
		map.put("seckillId", seckillId);
		map.put("phone", userPhone);
		map.put("killTime", killtime);
		map.put("result", null);
		//执行存储过程,result被赋值
		try {
			seckillDao.killByProcedure(map);
			int result = MapUtils.getInteger(map,"result",-2);
			if(result == 1){
				SuccessKilled sk = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
				return new SeckillExecution(seckillId,SeckillStateEnum.SUCCESS,sk);
			}else{
				return new SeckillExecution(seckillId,SeckillStateEnum.stateOf(result));
			}
		} catch (Exception e) {
			// TODO: handle exception
			return new SeckillExecution(seckillId,SeckillStateEnum.INNER_ERROR);
		}
	}

}
