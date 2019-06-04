package com.dffan.beans.context;

import java.util.Map;

public class AbstractApplicationContext implements ApplicationContext {

	@Override
	public Map<String, Object> createWebContext(String contextConfigLocation) {
		return null;
	}

}
