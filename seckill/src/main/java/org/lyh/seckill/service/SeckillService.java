package org.lyh.seckill.service;

import java.util.List;

import org.lyh.seckill.dto.Exposer;
import org.lyh.seckill.dto.SeckillExecution;
import org.lyh.seckill.entity.Seckill;
import org.lyh.seckill.exception.RepeatKillException;
import org.lyh.seckill.exception.SeckillCloseException;
import org.lyh.seckill.exception.SeckillException;

/**
 * 库存接口
 * 
 *业务接口:站在“使用者”角度设计接口，从别人的角度去思考这个接口该怎么写，传什么参数
 * 三个方面：方法定义粒度（非常清晰明确，比如秒杀这个service
 * 	要怎么做），参数（最好传递简练，明确的参数，避免传递Map这类大参），返回类型（return类型 友好，避免return Map类型，也可以return异常），
 * 
 * 上次实习所在的公司因为传递的业务数据过多，因此封装成了一个它自己设计的数据类（类似Map），因此这大概算是不友好的service接口，但是
 * 却方便了统一，因为所有service传递的参数类型都是那个类
 */
public interface SeckillService {
	/*
	 * 查询所有库存记录
	 */	 
	List<Seckill> getSeckillList();
	/*
	 *查询单个库存记录
	 */
	Seckill getById(long seckillId);
	/*
	 *输出秒杀接口地址 ,当秒杀开启的时候展现秒杀接口的地址，否则输入系统时间和秒杀时间
	 *出于安全角度考虑，这样就可以让秒杀未开始的时候所有人都猜不到秒杀的url，防止某些
	 *不怀好意的人使用浏览器插件辅佐秒杀~
	 *
	 *返回类型是dto层里面的Exposer，这个层设计的目的是为了封装一些与业务不相关的数据。
	 *方便我们做一些特殊的方法时的设计，比如这个方法
	 */
	Exposer exportSeckillUrl(long seckillId);
	
	/*
	 *执行秒杀操作
	 *传递进md5的原因是因为判断用户的url是否被修改~。如果被修改则不执行秒杀
	 *因为肯定是exportSeckillUrl这个方法先执行，所以用户肯定会生成一个md5
	 *
	 *声明抛出的异常是我们定义的异常，至于为何要声明抛出3个异常是因为可以根据这三个
	 *异常去声明不同的异常信息
	 */
	SeckillExecution executeSeckill(long seckillId,long userPhone,String md5) 
			throws SeckillException,SeckillCloseException,RepeatKillException;
	
	SeckillExecution executeSeckillProcedure(long seckillId,long userPhone,String md5);
	
}
