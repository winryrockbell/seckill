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
 * ���ӿ�ʵ����
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
	//md5��ֵ�ַ��������ڻ���MD5����������䣬Խ�޹���Խ��Խ��
	private final String slat="sajriwqo^95u3899^$";	
	public List<Seckill> getSeckillList() {
		return seckillDao.queryAll(0, 4);
	}

	public Seckill getById(long seckillId) {
		return seckillDao.queryById(seckillId);
	}

	public Exposer exportSeckillUrl(long seckillId) {
		//�����Ż����Ż���¶�ӿڣ�
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
		Date nowTime=new Date();//��ǰʱ��
//		Calendar calendar =new GregorianCalendar();
//		calendar.setTime(nowTime);
//		calendar.add(calendar.DATE, -1);
//		nowTime=calendar.getTime();		
		if(nowTime.getTime()<startTime.getTime()||nowTime.getTime()>endTime.getTime()){
			return new Exposer(false,seckillId,nowTime.getTime(),startTime.getTime(),endTime.getTime());
		}
		//ת���ض��ַ����Ĺ��̣�md5������ص��ǲ�����~
		String md5 = getMd5(seckillId);
		return new Exposer(true,md5,seckillId);
	}
	private String getMd5(long seckillId){
		String base = seckillId+"/"+slat;//������õ���������������ֵ�����û����ֵ�����п����ƽ�����㷨
		String md5 = DigestUtils.md5DigestAsHex(base.getBytes());//ͨ��spring�ṩ�Ĺ�������м���
		return md5;
	}
	/*
	 *ʹ��ע��������񷽷����ŵ�:
	 *1.�����Ŷӿ����γ�Լ������ȷ��׼���񷽷��ı�̷�񣨣� 
	 *2.��֤���񷽷���ִ��ʱ�価���̣ܶ���Ҫ����������������� ����HTTP����
	 *ʵ����Ҫ��Щ�����ͳ�������������뵽�����⣬����Щ���񷽷������ܸɾ�������ΪҪ����ɱϵͳ��
	 *3.�������еķ�������Ҫ��������ֻ��������ֻ��һ���޸Ĳ�����
	 */
	@Transactional
	public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
			throws SeckillException, SeckillCloseException, RepeatKillException {
		if(md5 == null || !(md5.equals(getMd5(seckillId)))){
			throw new SeckillException("�û���md5��Ϣ�쳣");
		}
		//ִ����ɱ�߼��������+��¼������Ϊ
		try {
			Date nowTime = new Date();
//			Calendar calendar =new GregorianCalendar();
//			calendar.setTime(nowTime);
//			calendar.add(calendar.DATE, -1);
//			nowTime=calendar.getTime();	
			
			int insertnum = successKilledDao.insertSuccessKilled(seckillId, userPhone);
			if(insertnum <= 0){
				//�ظ���ɱ
				throw new RepeatKillException("�ظ���ɱ��Ŷ");
			}else{
				//���¿�棬�ȵ���Ʒ�ľ���
				int updatenum=seckillDao.reduceNumber(seckillId, nowTime);
				if(updatenum <= 0){
					//��ɱ����,����ع�
					throw new SeckillCloseException("��ɱ������Ŷ");
				}else{
					//��ɱ�ɹ�,�����ύ
					SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
					return new SeckillExecution(seckillId,SeckillStateEnum.SUCCESS,successKilled);
				}
			}
		}catch(SeckillCloseException e1){
			logger.error(e1.getMessage(),e1);
			throw new SeckillCloseException("��ɱ������Ŷ");
		}catch(RepeatKillException e2){
			logger.error(e2.getMessage(),e2);
			throw new RepeatKillException("�ظ���ɱ��Ŷ");
		}catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new SeckillException("��ɱ�߼��ڲ��쳣");
		}
	}
	//ͨ���洢����ȥ������ɱ
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
		//ִ�д洢����,result����ֵ
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
