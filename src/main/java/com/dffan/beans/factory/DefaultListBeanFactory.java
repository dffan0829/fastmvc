package com.dffan.beans.factory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
	//����handlemappingӳ���ϵ ��fastmapping��Ŀ�귽������ӳ���ϵ
	private static Map<String,Object> handleMapping = new ConcurrentHashMap<>();

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
		if (IOC == null || IOC.isEmpty()) {
			IOC = applicationContext.createWebContext(contextConfigLocation);
		}
		return IOC;
	}
	
	/**
	 * ���������ļ�����BeanFactory
	 * @param contextConfigLocation
	 * @return
	 */
	public Map<String, Object> buildHandleMapping() {
		return applicationContext.buildHandleMapping();
	}
}