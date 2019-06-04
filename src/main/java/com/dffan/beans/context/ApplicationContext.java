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
	
	// 后期会加入一些其他创建容器的方式 比如 根据配置类 等
	
}
