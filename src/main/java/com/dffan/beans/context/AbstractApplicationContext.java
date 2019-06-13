package com.dffan.beans.context;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.dffan.annotation.FastComponent;
import com.dffan.annotation.FastController;
import com.dffan.annotation.FastMapping;

public class AbstractApplicationContext implements ApplicationContext {

	// 定义一个List存放所有的扫描包下面的classname 便于后期创建实例
	private static List<String> classNames = new ArrayList<>();
	// 定义一个IOC容器用于将控制器与实例对象建立对应关系
	private static Map<String, Object> IOC = new HashMap<>();
	// 请求和处理器之间的映射
	private static Map<String, Object> handleMapping = new HashMap<>();

	@Override
	public Map<String, Object> createWebContext(String contextConfigLocation) {
		String packageName = doLoadConfig(contextConfigLocation);
		// 扫描指定包下的所有类
		doScanner(packageName);
		// 创建IOC容器
		createIOC();
		return IOC;
	}

	public void doScanner(String packageName) {
		// 根据包名查找下面的所有类
		String path = packageName.replace(".", "/");
		URL resource = this.getClass().getClassLoader().getResource(path);
		File file = new File(resource.getFile());
		for (File f : file.listFiles()) {
			// 如果是个文件夹的话 继续递归扫描
			if (f.isDirectory()) {
				doScanner(packageName + "." + f.getName());
			} else {
				String classPath = packageName + "." + f.getName().replace(".class", "");
				classNames.add(classPath);
			}
		}
	}

	// 获取到请求和处理器的映射mapping
	@Override
	public Map<String, Object> getApplicationContext() {
		return IOC;
	}

	// 获取到请求和处理器的映射mapping
	@Override
	public Map<String, Object> getHandleMapping() {
		return handleMapping;
	}

	@Override
	// 请求url和处理器目标方法的映射
	public Map<String, Object> buildHandleMapping() {
		// ioc容器为空时候 退出
		if (IOC == null) {
			return null;
		}
		for (String className : classNames) {
			try {
				Class<?> clazz = Class.forName(className);
				// 如果当前类没有该注解的话 跳出
				if (!clazz.isAnnotationPresent(FastController.class)) {
					continue;
				}
				String classMapping = "";
				String methodMapping = "";
				if (clazz.isAnnotationPresent(FastMapping.class)) {
					classMapping = clazz.getAnnotation(FastMapping.class).value();
					Method[] methods = clazz.getDeclaredMethods();
					for (Method method : methods) {
						if (method.isAnnotationPresent(FastMapping.class)) {
							methodMapping = method.getAnnotation(FastMapping.class).value();
						}
						handleMapping.put(classMapping + methodMapping, method);
					}
				} else {
					Method[] methods = clazz.getMethods();
					for (Method method : methods) {
						if (method.isAnnotationPresent(FastMapping.class)) {
							methodMapping = method.getAnnotation(FastMapping.class).value();
						}
						handleMapping.put(methodMapping, method);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return handleMapping;
	}

	public void createIOC() {
		// 根据 classNames 创建对应的实例 必须加
		for (String classname : classNames) {
			try {
				Class<?> clazz = Class.forName(classname);
				String key = "";
				// 判断类上是否有控制器的注解
				if (clazz.isAnnotationPresent(FastController.class)) {
					String controllerName = clazz.getDeclaredAnnotation(FastController.class).value();
					if (controllerName != null && !controllerName.isEmpty()) {
						key = controllerName.toLowerCase();
					} else {
						key = clazz.getSimpleName().toLowerCase();
					}
				} else if (clazz.isAnnotationPresent(FastComponent.class)) {
					// 判断类上是否有组件的注解
					String componentName = clazz.getDeclaredAnnotation(FastComponent.class).value();
					if (componentName != null && !componentName.isEmpty()) {
						key = componentName.toLowerCase();
					} else {
						key = clazz.getSimpleName().toLowerCase();
					}
				}
				// 创建实例对象 并且放入IOC中
				IOC.put(key, clazz.newInstance());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public String doLoadConfig(String contextConfigLocation) {
		// 获取到要扫描的包
		InputStream is = this.getClass().getClassLoader()
				.getResourceAsStream(contextConfigLocation.replace("classpath:", ""));
		if (is == null) {
			try {
				throw new Exception("配置文件为空或者不存在！");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		SAXReader reader = new SAXReader();
		Document doc;
		String value = "";
		try {
			doc = reader.read(is);
			Element rootElement = doc.getRootElement();
			Element element = rootElement.element("component-scan");
			Attribute attribute = element.attribute("package");
			value = attribute.getValue();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		try {
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return value;
	}
}