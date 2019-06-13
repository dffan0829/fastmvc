package com.dffan.beans.context;

import java.util.Map;

/**
 * 当前的应用 为 web 容器 (IOC概念落地应用)
 * IOC容器总的接口
 * @author dff
 *
 */
public interface ApplicationContext {

	/**
	 * 根据 xml 配置文件创建IOC容器 配置文件里会指定自动扫描的包等
	 * @param contextConfigLocation
	 * @return
	 */
	public Map<String,Object> createWebContext(String contextConfigLocation);
	/**
	 * 建立请求和目标方法的映射关系
	 * @return
	 */
	public Map<String,Object>  buildHandleMapping();
	/**
	 * 获取到请求和处理器间的mapping映射
	 * @return
	 */
	public Map<String,Object>  getHandleMapping();
	/**
	 *  获取到当前的IOC容器
	 * @return
	 */
	public Map<String, Object> getApplicationContext();
}
