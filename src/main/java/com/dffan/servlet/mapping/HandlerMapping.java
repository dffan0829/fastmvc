package com.dffan.servlet.mapping;

import javax.servlet.http.HttpServletRequest;

import com.dffan.servlet.HandleExecutionChain;

public interface HandlerMapping {

	//���������url��ȡ��Ŀ�괦����
	HandleExecutionChain getHandler(HttpServletRequest request);
		
}
