package com.dffan.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dffan.beans.context.AbstractApplicationContext;
import com.dffan.beans.context.ApplicationContext;
import com.dffan.beans.factory.DefaultListBeanFactory;
import com.dffan.servlet.mapping.HandlerMapping;
import com.dffan.servlet.mapping.RequestMappingHandleMapping;

public class DispatcherServlet extends HttpServlet{
 
	private static final long serialVersionUID = 1L;
	
	@Override
	public void init(ServletConfig config) {
		String contextConfigLocation = config.getInitParameter("contextConfigLocation");
		
		ApplicationContext applicationContext = new AbstractApplicationContext();
		DefaultListBeanFactory defaultListBeanFactory = new DefaultListBeanFactory(applicationContext);
 		defaultListBeanFactory.buildBeanFactory(contextConfigLocation);
 		defaultListBeanFactory.buildHandleMapping();
	}

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HandlerMapping requestMappingHandleMapping = new RequestMappingHandleMapping();
		HandleExecutionChain handleExecutionChain = requestMappingHandleMapping.getHandler(request);
		try {
			if(!handleExecutionChain.preHandle(request, response)){
				return;
			}
			Object o = handleExecutionChain.handle(request, response);
			response.getWriter().print(o);
			handleExecutionChain.postHandle(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
