package com.dffan.beans.context;

import java.util.Map;

/**
 * ��ǰ��Ӧ�� Ϊ web ���� (IOC�������Ӧ��)
 * IOC�����ܵĽӿ�
 * @author dff
 *
 */
public interface ApplicationContext {

	/**
	 * ���� xml �����ļ�����IOC���� �����ļ����ָ���Զ�ɨ��İ���
	 * @param contextConfigLocation
	 * @return
	 */
	public Map<String,Object> createWebContext(String contextConfigLocation);
	
	// ���ڻ����һЩ�������������ķ�ʽ ���� ���������� ��
	
}
