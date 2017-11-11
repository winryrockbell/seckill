package org.lyh.seckill.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.lyh.seckill.entity.Seckill;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *配置spring和junit整合,junit启动时加载springIOC容器 
 *spring-test,junit
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
//告诉Junit spring配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SeckillDaoTest {
	//注入Dao实现依赖
	@Resource
	private SeckillDao seckillDao;
	@Test
	public void testQueryById() throws Exception{
		long id=1000;
		Seckill seckill=seckillDao.queryById(id);
		System.out.println(seckill.getName());
		System.out.println(seckill);
		/*
		 *1000元秒杀iphone6
			Seckill [seckillId=1000, name=1000元秒杀iphone6, 
					number=100, 
					startTime=Wed Nov 01 00:00:00 CST 2017, 
					endTime=Thu Nov 02 00:00:00 CST 2017, 
					createTime=Tue Oct 31 21:33:54 CST 2017]
 
		 */
	}
	@Test
	public void testQueryAll() throws Exception{
		/*org.mybatis.spring.MyBatisSystemException: nested exception is org.apache.ibatis.binding.BindingException: Parameter 'offset' not found. 
		 * Available parameters are [0, 1, param1, param2]
		 * java没有保存形参的记录，因此在运行queryAll（int offset,int limit)的时候，
		 * 实际上就会转换为queryAll（int arg1,int arg0)
		 * */
		List<Seckill> seckills=seckillDao.queryAll(0, 100);
		for(Seckill seckill:seckills){
			System.out.println(seckill);
		}
		
	}
	@Test
	public void testReduceNumber() throws Exception{
		Date killTime =new Date();//取时间
		Calendar calendar =new GregorianCalendar();
		calendar.setTime(killTime);
		calendar.add(calendar.DATE, 1);
		killTime=calendar.getTime();
		int num=seckillDao.reduceNumber(1000L,killTime);
		System.out.println(num);
		/*
		 *输出结果:
		 *10:44:32.608 [main] DEBUG o.m.s.t.SpringManagedTransaction - JDBC Connection [com.mchange.v2.c3p0.impl.NewProxyConnection@4681c175] will not be managed by Spring
		  10:44:32.613 [main] DEBUG o.l.s.dao.SeckillDao.reduceNumber - ==>  Preparing: update seckill set number = number-1 where seckill_id = ? and start_time <= ? and end_time >= ? and number > 0; 
		  10:44:32.642 [main] DEBUG o.l.s.dao.SeckillDao.reduceNumber - ==> Parameters: 1000(Long), 2017-11-03 10:44:32.295(Timestamp), 2017-11-03 10:44:32.295(Timestamp) 
		 * 
		 */
	}
}
