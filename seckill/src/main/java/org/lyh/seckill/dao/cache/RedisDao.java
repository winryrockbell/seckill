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
	//������ṹ�Ķ����������л���ʱ��ֵ
	private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);
	public RedisDao(String ip,int port){
		jedisPool = new JedisPool(ip,port);
	}
	public Seckill getSeckill(long seckillId){
		//redis�����߼�
		try {
			Jedis jedis = jedisPool.getResource();//�����ڻ�ȡ����
			try {
				String key = "seckill:" + seckillId;
				//Jedis��Redis��û��ʵ���ڲ����л�����
				//һ�㻺����ʶ�Ӧ����������: get ->byte[] ->�����л� ->�õ�Seckill����
				//�����Զ������л�������java�Լ���serializable����Ϊ�и������ܵ�ѡ��
				//protostuff�����ɵ����л��Ķ��������pojo����get��set����Ķ���
				byte[] bytes = jedis.get(key.getBytes());
				if(bytes != null){
					Seckill seckill = schema.newMessage();
					ProtostuffIOUtil.mergeFrom(bytes,seckill, schema);//seckill�ͱ������л���
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
		//  seckill  --> ���л�---->�ŵ�redis
		try {
			Jedis jedis =jedisPool.getResource();
			try {
					String key = "seckill:"+seckill.getSeckillId();
					byte[] bytes =ProtostuffIOUtil.toByteArray(seckill,schema,
							LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));//��������������̫��ʱ���軺��
					int time = 60 * 60;//����1Сʱ
					String result = jedis.setex(key.getBytes(), time, bytes);//�����ַ�����Ϣ�����㻺����û�гɹ�
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
