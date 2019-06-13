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
	/**
	 * ���������Ŀ�귽����ӳ���ϵ
	 * @return
	 */
	public Map<String,Object>  buildHandleMapping();
	/**
	 * ��ȡ������ʹ��������mappingӳ��
	 * @return
	 */
	public Map<String,Object>  getHandleMapping();
	/**
	 *  ��ȡ����ǰ��IOC����
	 * @return
	 */
	public Map<String, Object> getApplicationContext();
}
