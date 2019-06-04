package com.dffan.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Ŀ�귽��������  ���ڷ���ִ�е�ǰ��������� ������һЩ Ȩ��У��� 2019-6-4 17:17:42
 * @author dff
 *
 */
public interface HandleInterceptor {
	
	//Ŀ�귽������֮ǰ handler ΪĿ�괦����ʵ������
	boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
		    throws Exception;
	
	//Ŀ�귽������֮��
	boolean postHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
		    throws Exception;
}
