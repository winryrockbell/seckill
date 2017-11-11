package org.lyh.seckill.dao.cache;

import org.lyh.seckill.entity.Seckill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisDao {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private JedisPool jedisPool;
	//描述类结构的对象，用于序列化的时候赋值
	private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);
	public RedisDao(String ip,int port){
		jedisPool = new JedisPool(ip,port);
	}
	public Seckill getSeckill(long seckillId){
		//redis操作逻辑
		try {
			Jedis jedis = jedisPool.getResource();//类似于获取连接
			try {
				String key = "seckill:" + seckillId;
				//Jedis和Redis都没有实现内部序列化操作
				//一般缓存访问都应该是这样的: get ->byte[] ->反序列化 ->拿到Seckill对象
				//采用自定义序列化而不用java自己的serializable，因为有更高性能的选择
				//protostuff所接纳的序列化的对象必须是pojo（带get和set）类的对象
				byte[] bytes = jedis.get(key.getBytes());
				if(bytes != null){
					Seckill seckill = schema.newMessage();
					ProtostuffIOUtil.mergeFrom(bytes,seckill, schema);//seckill就被反序列化了
					return seckill;
				}
			} finally{
				jedis.close();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return null;
	}
	public String putSeckill(Seckill seckill){
		//  seckill  --> 序列化---->放到redis
		try {
			Jedis jedis =jedisPool.getResource();
			try {
					String key = "seckill:"+seckill.getSeckillId();
					byte[] bytes =ProtostuffIOUtil.toByteArray(seckill,schema,
							LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));//缓存器，当对象太大时给予缓冲
					int time = 60 * 60;//缓存1小时
					String result = jedis.setex(key.getBytes(), time, bytes);//返回字符串信息告诉你缓存有没有成功
					return result;
			} finally{
				jedis.close();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		
		return null;
	}
}
