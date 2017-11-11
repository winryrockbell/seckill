--���ݿ��ʼ���ű�

--�������ݿ�
create database seckill;
--ʹ�����ݿ�
use seckill;
--������ɱ����,����������Ϊ�˱������ű�ʾ��InnoDB�洢����ģ���������1000��ʼ
--�Լ����ı��� �����ע��(MYSQL��ֻ��InnoDB֧���������)
drop table seckill;
create table seckill(
`seckill_id` bigint NOT NULL AUTO_INCREMENT COMMENT '��Ʒ���id',
`name` varchar(120) NOT NULL COMMENT '��Ʒ����',
`number` int 		NOT NULL COMMENT '�������',
`create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '����ʱ��',
`start_time` timestamp NOT NULL COMMENT '��ɱ��ʼʱ��',
`end_time` timestamp NOT NULL COMMENT '��ɱ����ʱ��',
-- ��������DEFAULT��ָĬ�����ɵ����ݣ������ʱ����Բ����Լ���
-- ��������
PRIMARY KEY(seckill_id),
key idx_start_time(start_time),
key idx_end_time(end_time),
key idx_create_time(create_time)

)ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8mb4 COMMENT='��ɱ����';

--��ʼ������
insert into 
	seckill(name,number,start_time,end_time)
values
	('1000Ԫ��ɱiphone6',100,'2017-11-01 00:00:00','2017-11-02 00:00:00'),
	('300Ԫ��ɱipad2',500,'2017-11-01 00:00:00','2017-11-02 00:00:00'),
	('400Ԫ��ɱС��4',30,'2017-11-01 00:00:00','2017-11-02 00:00:00'),
	('200Ԫ��ɱ����note',300,'2017-11-01 00:00:00','2017-11-02 00:00:00');
	
--��ɱ�ɹ���ϸ��
--�û���¼��֤��ص���Ϣ
create table success_killed(
`seckill_id` bigint NOT NULL COMMENT '��ɱ��Ʒid',
`user_phone` bigint NOT NULL COMMENT '�û��ֻ���',
`state` tinyint NOT NULL DEFAULT -1 COMMENT '״̬��ʶ��-1:��Ч 0���ɹ� 1���Ѹ���  2���ѷ���',
`create_time` timestamp NOT NULL COMMENT '����ʱ��',
-- ��������
PRIMARY KEY(seckill_id,user_phone), -- ��������
key idx_create_time(create_time)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='��ɱ�ɹ���ϸ��';

--�������ݿ�Ŀ���̨
mysql -uroot -p

