package com.dffan;

import org.junit.Test;

import com.dffan.interceptor.FastInterceptorCglib;

public class InterceptorTest {

	@Test
	public void testInterceptor() {
		FastInterceptorCglib proxy = new FastInterceptorCglib();
		//通过生成子类的方式创建代理类
		MyClass1 proxyImp = (MyClass1)proxy.getProxy(MyClass1.class);
		proxyImp.mytest("211221");
	}
}


class MyClass1  {
	public void mytest(String aa) {
		System.out.println("mytest-----");
	}
}

class MyClass implements MyInterface {

	@Override
	public void mytest() {
		System.out.println("mytest-----");
	}
}
