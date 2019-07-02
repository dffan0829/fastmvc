package com.dffan.servlet;

import java.lang.reflect.Method;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dffan.interceptor.FastInterceptorCglib;
import com.dffan.interceptor.HandleInterceptor;

/**
 * 处理器执行链
 * 
 * @author dff
 *
 */
public class HandleExecutionChain {

	// 目标处理器
	private final Object handler;

	private HandleInterceptor[] handleInterceptors;

	private final List<Object> arrays;

	private final Method method;

	/**
	 * 构造函数 传入处理器 和 拦截器
	 * 
	 * @param handler
	 * @param handleInterceptor
	 */
	public HandleExecutionChain(Object handler, Method method, List<Object> l,  HandleInterceptor... handleInterceptor) {
		this.handler = handler;
		this.handleInterceptors = handleInterceptor;
		this.arrays = l;
		this.method = method;
	}


	/**
	 * 处理器的 preHandle方法 在目标方法执行前 执行
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response) throws Exception {
		for (HandleInterceptor handleInterceptor : handleInterceptors) {
			boolean flag = handleInterceptor.preHandle(request, response, this.handler);
			if (!flag) {
				return false;
			}
		}
		return true;
	}

	/***
	 * 执行目标方法
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public Object handle(HttpServletRequest request, HttpServletResponse response) {
		FastInterceptorCglib proxy = new FastInterceptorCglib();
		Object obj = proxy.getProxy(handler.getClass());
		Object o = null;
		try {
			o = method.invoke(obj, arrays.toArray());
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return o;
	}

	/**
	 * 处理器的 postHandle方法 在目标方法执行后 执行
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public boolean postHandle(HttpServletRequest request, HttpServletResponse response) throws Exception {
		for (HandleInterceptor handleInterceptor : handleInterceptors) {
			boolean flag = handleInterceptor.postHandle(request, response, this.handler);
			if (!flag) {
				return false;
			}
		}
		return true;
	}
}