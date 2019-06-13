package com.dffan.servlet.mapping;

import javax.servlet.http.HttpServletRequest;

import com.dffan.servlet.HandleExecutionChain;

public interface HandlerMapping {

	//根据请求的url获取到目标处理器
	HandleExecutionChain getHandler(HttpServletRequest request);
		
}
