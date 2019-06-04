package com.dffan.interceptor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class FastInterceptor implements InvocationHandler{

	private Object obj;
	
	public FastInterceptor() {
	}
	
	public FastInterceptor(Object obj) {
		 this.obj = obj;
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		System.out.println("aaaaaaa");
		method.invoke(obj, args);
		System.out.println("bbbbbbb");
		return null;
	}
}
