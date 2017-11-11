package org.lyh.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.lyh.seckill.entity.SuccessKilled;

public interface SuccessKilledDao {
	
	//插入秒杀明细记录，可过滤重复
	int insertSuccessKilled(@Param("seckillId")long seckillId,@Param("userPhone")long userPhone);
	//根据库存id查询该商品的秒杀记录 并且附带库存实体
	SuccessKilled queryByIdWithSeckill(@Param("seckillId")long seckillId,@Param("userPhone")long userPhone);
}
