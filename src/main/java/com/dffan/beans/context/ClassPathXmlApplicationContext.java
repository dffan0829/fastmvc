package com.dffan.beans.context;

import java.util.Map;
/**
 * 此处用到了 适配器设计模式
 * @author dff
 *
 */
public class ClassPathXmlApplicationContext extends AbstractApplicationContext {

	@Override
	public Map<String, Object> createWebContext(String contextConfigLocation) {
	 
		return null;
	}
	 
	
}
