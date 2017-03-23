package com.android.wako.net.exception;

public class BaseException extends Exception {
	private int code ;
	public BaseException(int code){
		super();
		this.code = code;
	}
	
	public BaseException(int code,String msg){
		super(msg);
		this.code = code;
	}
	
	public BaseException(int code,String msg,Throwable throwable){
		super(msg,throwable);
		this.code = code;
	}
	
	public BaseException(int code,Throwable throwable){
		super(throwable);
		this.code = code;
	}
}
