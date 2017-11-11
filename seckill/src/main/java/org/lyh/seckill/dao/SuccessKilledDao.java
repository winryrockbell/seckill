package org.lyh.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.lyh.seckill.entity.SuccessKilled;

public interface SuccessKilledDao {
	
	//������ɱ��ϸ��¼���ɹ����ظ�
	int insertSuccessKilled(@Param("seckillId")long seckillId,@Param("userPhone")long userPhone);
	//���ݿ��id��ѯ����Ʒ����ɱ��¼ ���Ҹ������ʵ��
	SuccessKilled queryByIdWithSeckill(@Param("seckillId")long seckillId,@Param("userPhone")long userPhone);
}
