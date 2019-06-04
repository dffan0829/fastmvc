package com.dffan.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 目标方法拦截器  将在方法执行的前后进行拦截 可用于一些 权限校验等 2019-6-4 17:17:42
 * @author dff
 *
 */
public interface HandleInterceptor {
	
	//目标方法调用之前 handler 为目标处理器实例对象
	boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
		    throws Exception;
	
	//目标方法调用之后
	boolean postHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
		    throws Exception;
}
