package com.dffan.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dffan.interceptor.HandleInterceptor;

/**
 * ������ִ����
 * @author dff
 *
 */
public class HandleExecutionChain {

	//Ŀ�괦����
	private final Object handler;
	
	private HandleInterceptor [] handleInterceptors;
	
	/**
	 * ���캯�� ���봦���� �� ������
	 * @param handler
	 * @param handleInterceptor
	 */
	public HandleExecutionChain(Object handler,HandleInterceptor ... handleInterceptor ) {
		this.handler = handler;
		this.handleInterceptors = handleInterceptor;
	}
	
	/**
	 * �������� preHandle���� ��Ŀ�귽��ִ��ǰ ִ��
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public boolean preHandle(HttpServletRequest request,HttpServletResponse response) throws Exception{
		for (HandleInterceptor handleInterceptor : handleInterceptors) {
			boolean flag = handleInterceptor.preHandle(request, response, this.handler);
			if(!flag){
				return false;
			}
		}
		return true;
	}
	
	/***
	 * ִ��Ŀ�귽��
	 * @param request
	 * @param response
	 * @return
	 */
	public String handle(HttpServletRequest request,HttpServletResponse response){
		 return null;
	}
 
	/**
	 * �������� postHandle���� ��Ŀ�귽��ִ�к� ִ��
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public boolean postHandle(HttpServletRequest request,HttpServletResponse response) throws Exception{
		for (HandleInterceptor handleInterceptor : handleInterceptors) {
			boolean flag = handleInterceptor.postHandle(request, response, this.handler);
			if(!flag){
				return false;
			}
		}
		return true;
	}
	
}
