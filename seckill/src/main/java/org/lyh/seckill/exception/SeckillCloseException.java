package org.lyh.seckill.exception;
/*
 *秒杀关闭异常 (运行时异常），继承秒杀异常
 */
public class SeckillCloseException extends SeckillException{

	public SeckillCloseException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public SeckillCloseException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}
	
}
