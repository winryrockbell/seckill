package org.lyh.seckill.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.lyh.seckill.entity.Seckill;

public interface SeckillDao {
	//减少库存
	int reduceNumber(@Param("seckillId")long seckillId,@Param("killTime")Date killTime);
	//根据库存id查询库存信息
	Seckill queryById(long seckillId);
	//根据偏移量查询库存,这个@param是用来让mybatis执行语句时识别形参的~
	//当多个参数时最好这么做，因为java的设计上是不记录形参的.
	List<Seckill> queryAll(@Param("offset")int offset, @Param("limit")int limit);
	//根据存储过程查找
	void killByProcedure(Map<String,Object> map);
}
