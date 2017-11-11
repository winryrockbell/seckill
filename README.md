# seckill秒杀模块
基于spring集成springmvc集成mybatis的秒杀功能模块
## 使用到的技术集成如下
> spring+springmvc+mybatis <br>
> Redis服务作为缓存 <br>
> MySql <br>
> jQuery与bootstrap <br>
> 考虑后期加入Nginx反向代理服务
##优化过程如下
总体目标:降低java程序与mysql数据库之间的网络延迟以及java gc的影响，提升Mysql服务的QPS <br>
1.注意事务里的语句的执行顺序，让不需要持有行级锁的语句先走。 <br>
2.使用MySql的存储过程去管理事务的提交与回滚，在逻辑不算太复杂的时候可以使用。(本功能采取的方式)<br>
3.使用Redis等nosql的原子计数器配合Message Queue（消息队列）来修改最终数据库（利用Redis集群以及抗并发能力非常强的特点，互联网公司采取的解决方案之一）.


