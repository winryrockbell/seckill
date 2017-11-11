package org.lyh.seckill.service;

import java.util.List;

import org.lyh.seckill.dto.Exposer;
import org.lyh.seckill.dto.SeckillExecution;
import org.lyh.seckill.entity.Seckill;
import org.lyh.seckill.exception.RepeatKillException;
import org.lyh.seckill.exception.SeckillCloseException;
import org.lyh.seckill.exception.SeckillException;

/**
 * ���ӿ�
 * 
 *ҵ��ӿ�:վ�ڡ�ʹ���ߡ��Ƕ���ƽӿڣ��ӱ��˵ĽǶ�ȥ˼������ӿڸ���ôд����ʲô����
 * �������棺�����������ȣ��ǳ�������ȷ��������ɱ���service
 * 	Ҫ��ô��������������ô��ݼ�������ȷ�Ĳ��������⴫��Map�����Σ����������ͣ�return���� �Ѻã�����return Map���ͣ�Ҳ����return�쳣����
 * 
 * �ϴ�ʵϰ���ڵĹ�˾��Ϊ���ݵ�ҵ�����ݹ��࣬��˷�װ����һ�����Լ���Ƶ������ࣨ����Map��������������ǲ��Ѻõ�service�ӿڣ�����
 * ȴ������ͳһ����Ϊ����service���ݵĲ������Ͷ����Ǹ���
 */
public interface SeckillService {
	/*
	 * ��ѯ���п���¼
	 */	 
	List<Seckill> getSeckillList();
	/*
	 *��ѯ��������¼
	 */
	Seckill getById(long seckillId);
	/*
	 *�����ɱ�ӿڵ�ַ ,����ɱ������ʱ��չ����ɱ�ӿڵĵ�ַ����������ϵͳʱ�����ɱʱ��
	 *���ڰ�ȫ�Ƕȿ��ǣ������Ϳ�������ɱδ��ʼ��ʱ�������˶��²�����ɱ��url����ֹĳЩ
	 *�����������ʹ����������������ɱ~
	 *
	 *����������dto�������Exposer���������Ƶ�Ŀ����Ϊ�˷�װһЩ��ҵ����ص����ݡ�
	 *����������һЩ����ķ���ʱ����ƣ������������
	 */
	Exposer exportSeckillUrl(long seckillId);
	
	/*
	 *ִ����ɱ����
	 *���ݽ�md5��ԭ������Ϊ�ж��û���url�Ƿ��޸�~��������޸���ִ����ɱ
	 *��Ϊ�϶���exportSeckillUrl���������ִ�У������û��϶�������һ��md5
	 *
	 *�����׳����쳣�����Ƕ�����쳣������Ϊ��Ҫ�����׳�3���쳣����Ϊ���Ը���������
	 *�쳣ȥ������ͬ���쳣��Ϣ
	 */
	SeckillExecution executeSeckill(long seckillId,long userPhone,String md5) 
			throws SeckillException,SeckillCloseException,RepeatKillException;
	
	SeckillExecution executeSeckillProcedure(long seckillId,long userPhone,String md5);
	
}
