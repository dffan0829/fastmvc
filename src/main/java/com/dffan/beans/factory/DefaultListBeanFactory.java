package com.dffan.beans.factory;

import java.util.HashMap;
import java.util.Map;

import com.dffan.beans.context.ApplicationContext;

/**
 * bean������б� ���е�Bean��map��� ����IOC���� ��IOCʹ��Map
 * 
 * @author dff
 *
 */
public class DefaultListBeanFactory implements BeanFactory {

	private ApplicationContext applicationContext;

	// ����һ��IOC�������ڽ���������ʵ����������Ӧ��ϵ
	private static Map<String, Object> IOC = new HashMap<>();

	public DefaultListBeanFactory() {
	}

	public DefaultListBeanFactory(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	/**
	 * ���������ļ�����BeanFactory
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
