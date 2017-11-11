package org.lyh.seckill.entity;

import java.util.Date;
/**
 * 成功秒杀的明细表
 * 描述了成功被秒杀的商品的记录表 
 */
public class SuccessKilled {
	private long seckillId;
	private long userPhone;
	private short state;
	private Date createTime;
	
	//变通
	//一个秒杀商品可以被多次秒杀，因此这两个实体是属于一对多的关系。
	private Seckill seckill;
	
	public Seckill getSeckill() {
		return seckill;
	}
	public void setSeckill(Seckill seckill) {
		this.seckill = seckill;
	}
	public long getSeckillId() {
		return seckillId;
	}
	public void setSeckillId(long seckillId) {
		this.seckillId = seckillId;
	}
	public long getUserPhone() {
		return userPhone;
	}
	public void setUserPhone(long userPhone) {
		this.userPhone = userPhone;
	}
	public short getState() {
		return state;
	}
	public void setState(short state) {
		this.state = state;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	@Override
	public String toString() {
		return "SuccessKilled [seckillId=" + seckillId + ", userPhone=" + userPhone + ", state=" + state
				+ ", createTime=" + createTime + "]";
	}
	
}
