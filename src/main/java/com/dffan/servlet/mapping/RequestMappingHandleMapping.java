package com.dffan.servlet.mapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.dffan.annotation.FastController;
import com.dffan.annotation.FastParam;
import com.dffan.beans.context.AbstractApplicationContext;
import com.dffan.beans.context.ApplicationContext;
import com.dffan.interceptor.HandleInterceptor;
import com.dffan.servlet.HandleExecutionChain;

public class RequestMappingHandleMapping implements HandlerMapping {

	private static Map<String, Object> IOC = new HashMap<String, Object>();

	{
		IOC = new AbstractApplicationContext().getApplicationContext();
	}

	@Override
	public HandleExecutionChain getHandler(HttpServletRequest request) {
		String url = request.getRequestURI().replace(request.getContextPath(), "");
		ApplicationContext applicationContext = new AbstractApplicationContext();
		// 获取到所有的请求参数
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		Enumeration<String> parameterNames = request.getParameterNames();
		if (parameterNames != null) {
			while (parameterNames.hasMoreElements()) {
				String paraName = parameterNames.nextElement();
				String paraVal = request.getParameter(paraName);
				map.put(paraName, paraVal);
			}
		}
		Method method = (Method) applicationContext.getHandleMapping().get(url);
		String controllerName = method.getDeclaringClass().getAnnotation(FastController.class).value().toLowerCase();

		List<Object> l = new ArrayList<Object>();
		try {
			Annotation[] parameterAnnotations = method.getDeclaredAnnotations();
			if (parameterAnnotations == null || parameterAnnotations.length == 0) {
				throw new Exception("没有定义参数注解！");
			}
			// 存放着映射请求参数的具体值

			Parameter[] parameters = method.getParameters();
			for (Parameter parameter : parameters) {
				Annotation[] annotations = parameter.getAnnotations();
				for (Annotation annotation : annotations) {
					if (annotation instanceof FastParam) {
						String value = ((FastParam) annotation).value();
						if (map.containsKey(value)) {
							l.add(map.get(value));
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// 判断IOC容器中是否有handleInterceptor 组件
		Collection<Object> instance = IOC.values();
		List<HandleInterceptor> handleInterceptors = new ArrayList<HandleInterceptor>();
		for (Object obj : instance) {
			if (obj instanceof HandleInterceptor) {
				HandleInterceptor handleInterceptor = (HandleInterceptor) obj;
				handleInterceptors.add(handleInterceptor);
			}
		}

		return new HandleExecutionChain(applicationContext.getApplicationContext().get(controllerName),
				method , l , handleInterceptors.toArray(new HandleInterceptor[handleInterceptors.size()]));
	}

}
