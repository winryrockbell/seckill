package org.lyh.seckill.service;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.lyh.seckill.dto.Exposer;
import org.lyh.seckill.dto.SeckillExecution;
import org.lyh.seckill.entity.Seckill;
import org.lyh.seckill.exception.RepeatKillException;
import org.lyh.seckill.exception.SeckillCloseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml",
					   "classpath:spring/spring-service.xml"})
public class SeckillServiceTest {
	
	/*在做到这一步的时候，你才回想起：我日志的配置还没配呢！想起来这个demo使用
	 * logback这套日志框架的，那我们就去官网找标准配置吧~
	 * 
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private SeckillService seckillService;
	@Test
	public void testGetSeckillList() throws Exception{
		List<Seckill> list=seckillService.getSeckillList();
		logger.info("list={}",list);
	}
	@Test
	public void testGetById() throws Exception{
		long id = 1000L;
		Seckill seckill=seckillService.getById(id);
		logger.info("seckill={}",seckill);
	}
	@Test
	public void testExportSeckillUrl() throws Exception{
		long id = 1001L;
		Exposer exposer=seckillService.exportSeckillUrl(id);
		if(exposer.isExposed()){
			logger.info("Exposer={}",exposer);
			long phone = 18902410940L;
			String md5 = exposer.getMd5();
			try {
				SeckillExecution execution = seckillService.executeSeckill(id, phone, md5);
				logger.info("execution={}",execution);
			} catch (RepeatKillException e) {
				logger.error(e.getMessage());
			}catch (SeckillCloseException e){
				logger.error(e.getMessage());
			}
		}else{
			logger.warn("Exposer={}",exposer);
		}
	}
	@Test
	public void testKillByProcedure(){
		long id = 1003;
		long phone = 13380523540L;
		Exposer exposer = seckillService.exportSeckillUrl(id);
		if(exposer.isExposed()){
			String md5 = exposer.getMd5();
			SeckillExecution s = seckillService.executeSeckillProcedure(id, phone, md5);
			logger.info(s.getStateInfo());
		}
	}
}
