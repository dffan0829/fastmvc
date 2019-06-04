package com.dffan.interceptor;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class FastInterceptorCglib implements MethodInterceptor {

	private Enhancer enhancer = new Enhancer();

	public Object getProxy(Class clazz) {
		// ������Ҫ�����������
		enhancer.setSuperclass(clazz);
		enhancer.setCallback(this);
		// ͨ���ֽ��뼼����̬��������ʵ��
		return enhancer.create();
	}

	@Override
	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		System.out.println("������ʼִ��---------");
		// ͨ����������ø����еķ���
		Object result = proxy.invokeSuper(obj, args);
		System.out.println("����ִ�н���---------");
		return result;
	}
}
