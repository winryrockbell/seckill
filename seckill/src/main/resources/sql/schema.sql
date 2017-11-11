--数据库初始化脚本

--创建数据库
create database seckill;
--使用数据库
use seckill;
--创建秒杀库存表,后面的语句是为了表明这张表示用InnoDB存储引擎的，自增长从1000开始
--以及它的编码 和相关注释(MYSQL中只有InnoDB支持事务机制)
drop table seckill;
create table seckill(
`seckill_id` bigint NOT NULL AUTO_INCREMENT COMMENT '商品库存id',
`name` varchar(120) NOT NULL COMMENT '商品名称',
`number` int 		NOT NULL COMMENT '库存数量',
`create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
`start_time` timestamp NOT NULL COMMENT '秒杀开始时间',
`end_time` timestamp NOT NULL COMMENT '秒杀结束时间',
-- 上面的这个DEFAULT是指默认生成的数据，插入的时候可以不用自己插
-- 创建索引
PRIMARY KEY(seckill_id),
key idx_start_time(start_time),
key idx_end_time(end_time),
key idx_create_time(create_time)

)ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8mb4 COMMENT='秒杀库存表';

--初始化数据
insert into 
	seckill(name,number,start_time,end_time)
values
	('1000元秒杀iphone6',100,'2017-11-01 00:00:00','2017-11-02 00:00:00'),
	('300元秒杀ipad2',500,'2017-11-01 00:00:00','2017-11-02 00:00:00'),
	('400元秒杀小米4',30,'2017-11-01 00:00:00','2017-11-02 00:00:00'),
	('200元秒杀红米note',300,'2017-11-01 00:00:00','2017-11-02 00:00:00');
	
--秒杀成功明细表
--用户登录认证相关的信息
create table success_killed(
`seckill_id` bigint NOT NULL COMMENT '秒杀商品id',
`user_phone` bigint NOT NULL COMMENT '用户手机号',
`state` tinyint NOT NULL DEFAULT -1 COMMENT '状态标识，-1:无效 0：成功 1：已付款  2：已发货',
`create_time` timestamp NOT NULL COMMENT '创建时间',
-- 创建索引
PRIMARY KEY(seckill_id,user_phone), -- 联合主键
key idx_create_time(create_time)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='秒杀成功明细表';

--连接数据库的控制台
mysql -uroot -p

