package com.dffan.interceptor;

import java.lang.reflect.Method;

import com.alibaba.fastjson.JSON;
import com.dffan.annotation.FastResponseBody;

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

	//cglib��̬����ִ�з��� �ڷ���ִ���õ�����ֵ�� �ж��Ƿ���responsebodyע�� �еĻ���ֵתΪjson��ʽ����
	@Override
	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		System.out.println("������ʼִ��---------");
		// ͨ����������ø����еķ���
		Object result = proxy.invokeSuper(obj, args);
		//�����responsebodyע�� �򷵻�json�ַ���
		if(method.isAnnotationPresent(FastResponseBody.class)){
			result = JSON.toJSONString(result);
		}
		System.out.println("����ִ�н���---------");
		return result;
	}
}
