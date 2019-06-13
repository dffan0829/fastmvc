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

	// ����һ��List������е�ɨ��������classname ���ں��ڴ���ʵ��
	private static List<String> classNames = new ArrayList<>();
	// ����һ��IOC�������ڽ���������ʵ����������Ӧ��ϵ
	private static Map<String, Object> IOC = new HashMap<>();
	// ����ʹ�����֮���ӳ��
	private static Map<String, Object> handleMapping = new HashMap<>();

	@Override
	public Map<String, Object> createWebContext(String contextConfigLocation) {
		String packageName = doLoadConfig(contextConfigLocation);
		// ɨ��ָ�����µ�������
		doScanner(packageName);
		// ����IOC����
		createIOC();
		return IOC;
	}

	public void doScanner(String packageName) {
		// ���ݰ������������������
		String path = packageName.replace(".", "/");
		URL resource = this.getClass().getClassLoader().getResource(path);
		File file = new File(resource.getFile());
		for (File f : file.listFiles()) {
			// ����Ǹ��ļ��еĻ� �����ݹ�ɨ��
			if (f.isDirectory()) {
				doScanner(packageName + "." + f.getName());
			} else {
				String classPath = packageName + "." + f.getName().replace(".class", "");
				classNames.add(classPath);
			}
		}
	}

	// ��ȡ������ʹ�������ӳ��mapping
	@Override
	public Map<String, Object> getApplicationContext() {
		return IOC;
	}

	// ��ȡ������ʹ�������ӳ��mapping
	@Override
	public Map<String, Object> getHandleMapping() {
		return handleMapping;
	}

	@Override
	// ����url�ʹ�����Ŀ�귽����ӳ��
	public Map<String, Object> buildHandleMapping() {
		// ioc����Ϊ��ʱ�� �˳�
		if (IOC == null) {
			return null;
		}
		for (String className : classNames) {
			try {
				Class<?> clazz = Class.forName(className);
				// �����ǰ��û�и�ע��Ļ� ����
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
		// ���� classNames ������Ӧ��ʵ�� �����
		for (String classname : classNames) {
			try {
				Class<?> clazz = Class.forName(classname);
				String key = "";
				// �ж������Ƿ��п�������ע��
				if (clazz.isAnnotationPresent(FastController.class)) {
					String controllerName = clazz.getDeclaredAnnotation(FastController.class).value();
					if (controllerName != null && !controllerName.isEmpty()) {
						key = controllerName.toLowerCase();
					} else {
						key = clazz.getSimpleName().toLowerCase();
					}
				} else if (clazz.isAnnotationPresent(FastComponent.class)) {
					// �ж������Ƿ��������ע��
					String componentName = clazz.getDeclaredAnnotation(FastComponent.class).value();
					if (componentName != null && !componentName.isEmpty()) {
						key = componentName.toLowerCase();
					} else {
						key = clazz.getSimpleName().toLowerCase();
					}
				}
				// ����ʵ������ ���ҷ���IOC��
				IOC.put(key, clazz.newInstance());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public String doLoadConfig(String contextConfigLocation) {
		// ��ȡ��Ҫɨ��İ�
		InputStream is = this.getClass().getClassLoader()
				.getResourceAsStream(contextConfigLocation.replace("classpath:", ""));
		if (is == null) {
			try {
				throw new Exception("�����ļ�Ϊ�ջ��߲����ڣ�");
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