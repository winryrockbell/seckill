package org.lyh.seckill.dao;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.lyh.seckill.entity.SuccessKilled;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//告诉junit加载spring配置文件，生成好对应的bean对象
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKilledDaoTest {
	@Resource
	private SuccessKilledDao successKilledDao;
	@Test
	public void testinsertSuccessKilled(){
		long id=1000L;
		long phone=13380523540L;
		int num=successKilledDao.insertSuccessKilled(id,phone);
		System.out.println(num);
		/*第一次执行结果：
		 * 10:54:22.246 [main] DEBUG o.l.s.d.S.insertSuccessKilled - ==>  Preparing: insert ignore into success_killed(seckill_id,user_phone) values (?,?) 
		 * 10:54:22.275 [main] DEBUG o.l.s.d.S.insertSuccessKilled - ==> Parameters: 1000(Long), 13380523540(Long)
		 * 10:54:22.280 [main] DEBUG o.l.s.d.S.insertSuccessKilled - <==    Updates: 1
		 */
		/*
		 *第二次执行结果:
		 *10:56:30.995 [main] DEBUG o.l.s.d.S.insertSuccessKilled - ==>  Preparing: insert ignore into success_killed(seckill_id,user_phone) values (?,?) 
		  10:56:31.026 [main] DEBUG o.l.s.d.S.insertSuccessKilled - ==> Parameters: 1000(Long), 13380523540(Long)
		  10:56:31.027 [main] DEBUG o.l.s.d.S.insertSuccessKilled - <==    Updates: 0
		 * 第二次插入失败，原因是设计这张记录表的时候是用联合主键保证唯一的~。还有sql语句用了ignore避免了抛出异常的结果~
		 */
	}
	@Test
	public void testqueryByIdWithSeckill(){
		long id=1000L;
		long phone=13380523540L;
		SuccessKilled s=successKilledDao.queryByIdWithSeckill(id, phone);
		System.out.println(s);
		System.out.println(s.getSeckill());
	}
}
