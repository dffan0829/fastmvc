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
		// 设置需要创建子类的类
		enhancer.setSuperclass(clazz);
		enhancer.setCallback(this);
		// 通过字节码技术动态创建子类实例
		return enhancer.create();
	}

	//cglib动态代理执行方法 在方法执行拿到返回值后 判断是否有responsebody注解 有的话则将值转为json格式返回
	@Override
	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		System.out.println("方法开始执行---------");
		// 通过代理类调用父类中的方法
		Object result = proxy.invokeSuper(obj, args);
		//如果有responsebody注解 则返回json字符串
		if(method.isAnnotationPresent(FastResponseBody.class)){
			result = JSON.toJSONString(result);
		}
		System.out.println("方法执行结束---------");
		return result;
	}
}
