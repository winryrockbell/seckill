package org.lyh.seckill.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.lyh.seckill.entity.Seckill;

public interface SeckillDao {
	//���ٿ��
	int reduceNumber(@Param("seckillId")long seckillId,@Param("killTime")Date killTime);
	//���ݿ��id��ѯ�����Ϣ
	Seckill queryById(long seckillId);
	//����ƫ������ѯ���,���@param��������mybatisִ�����ʱʶ���βε�~
	//���������ʱ�����ô������Ϊjava��������ǲ���¼�βε�.
	List<Seckill> queryAll(@Param("offset")int offset, @Param("limit")int limit);
	//���ݴ洢���̲���
	void killByProcedure(Map<String,Object> map);
}
