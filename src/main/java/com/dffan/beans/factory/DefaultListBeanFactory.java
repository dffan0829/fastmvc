package com.dffan.beans.factory;

import java.util.HashMap;
import java.util.Map;

import com.dffan.beans.context.ApplicationContext;

/**
 * bean定义的列表 所有的Bean用map存放 放入IOC容器 即IOC使个Map
 * 
 * @author dff
 *
 */
public class DefaultListBeanFactory implements BeanFactory {

	private ApplicationContext applicationContext;

	// 定义一个IOC容器用于将控制器与实例对象建立对应关系
	private static Map<String, Object> IOC = new HashMap<>();

	public DefaultListBeanFactory() {
	}

	public DefaultListBeanFactory(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	/**
	 * 根据配置文件构建BeanFactory
	 * @param contextConfigLocation
	 * @return
	 */
	public Map<String, Object> buildBeanFactory(String contextConfigLocation) {
		if (IOC == null) {
			IOC = applicationContext.createWebContext(contextConfigLocation);
		}
		return IOC;
	}
	
	

}
