package com.dffan.servlet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.hamcrest.core.IsInstanceOf;

import com.dffan.annotation.FastComponent;
import com.dffan.annotation.FastController;
import com.dffan.annotation.FastMapping;
import com.dffan.annotation.FastParam;
import com.dffan.interceptor.FastInterceptorCglib;
import com.dffan.interceptor.HandleInterceptor;

public class DispatcherServlet extends HttpServlet{
 
	private static final long serialVersionUID = 1L;
	
	//����һ��List������е�ɨ��������classname ���ں��ڴ���ʵ��
	private static List<String> classNames = new ArrayList<>();
	//����һ��IOC�������ڽ���������ʵ����������Ӧ��ϵ
	private static Map<String,Object> IOC = new HashMap<>();
	//����handlemappingӳ���ϵ ��fastmapping��Ŀ�귽������ӳ���ϵ
	private static Map<String,Object> handleMapping = new HashMap<>();

	
	//��ܳ�ʼ������ڷ��� ��ɨ��ָ���İ��µ������� �õ���Ӧ��ע����Ϣ ��ע����Ϣ�е�ֵ��Ŀ�괦�����ķ�����һ��ӳ�� 
	@Override
	public void init(ServletConfig config) {
		String contextConfigLocation = config.getInitParameter("contextConfigLocation");
		//���������ļ� ��ȡ�������ļ��е�ֵ �Զ�ɨ��ı���
		String packageName = doLoadConfig(contextConfigLocation);
		//ɨ��ָ�����µ�������
		doScanner(packageName);
		//����IOC����
		createIOC();
		//����handlemappingӳ���ϵ ��fastmapping��Ŀ�귽������ӳ���ϵ
		buildHandleMapping();
		
		//outPutInfo();
	
	}

	public void buildHandleMapping() {
		//ioc����Ϊ��ʱ�� �˳�
		if(IOC == null){
			return ;
		}
		for (String className : classNames) {
			try {
				Class<?> clazz = Class.forName(className);
				//�����ǰ��û�и�ע��Ļ� ����
				if(!clazz.isAnnotationPresent(FastController.class)){
					continue;
				}
				String classMapping = "";
				String methodMapping = "";
				if(clazz.isAnnotationPresent(FastMapping.class)){
					classMapping = clazz.getAnnotation(FastMapping.class).value();
					Method[] methods = clazz.getDeclaredMethods();
					for (Method method : methods) {
						if(method.isAnnotationPresent(FastMapping.class)){
							methodMapping = method.getAnnotation(FastMapping.class).value();
						}
						handleMapping.put(classMapping + methodMapping, method);
					}
				}else{
					Method[] methods = clazz.getMethods();
					for (Method method : methods) {
						if(method.isAnnotationPresent(FastMapping.class)){
							methodMapping = method.getAnnotation(FastMapping.class).value();
						}
						handleMapping.put(methodMapping, method);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void createIOC(){
		//���� classNames ������Ӧ��ʵ�� �����
		for (String classname : classNames) {
			try {
				Class<?> clazz = Class.forName(classname);
				String key = "";
				  //�ж������Ƿ��п�������ע��
				if(clazz.isAnnotationPresent(FastController.class)){
					String controllerName = clazz.getDeclaredAnnotation(FastController.class).value();
					if(controllerName!= null && !controllerName.isEmpty()){
						key = controllerName.toLowerCase();
					}else{
						key = clazz.getSimpleName().toLowerCase();
					}
				}else if(clazz.isAnnotationPresent(FastComponent.class)){
					//�ж������Ƿ��������ע��
					String componentName = clazz.getDeclaredAnnotation(FastComponent.class).value();
					if(componentName!= null && !componentName.isEmpty()){
						key = componentName.toLowerCase();
					}else{
						key = clazz.getSimpleName().toLowerCase();
					}
				} 
				//����ʵ������ ���ҷ���IOC��
				IOC.put(key, clazz.newInstance());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String url = request.getRequestURI().replace(request.getContextPath(), "");
		//�ж��Ƿ�����ӳ��� û���е� ����servlet����
		if(!handleMapping.containsKey(url)){
			super.service(request, response);
		}else{
			//��ȡ�����е��������
			Map<String,Object> map = new LinkedHashMap<>();
			Enumeration<String> parameterNames = request.getParameterNames();
			if(parameterNames !=null){
				while(parameterNames.hasMoreElements()){
					String paraName = parameterNames.nextElement();
					String paraVal = request.getParameter(paraName);
					map.put(paraName, paraVal);
				}
			}
			//���������������key��������
			Object[] keyArray = map.keySet().toArray();
			
			Method method = (Method) handleMapping.get(url);
			String controllerName = method.getDeclaringClass().getAnnotation(FastController.class).value().toLowerCase();
			//�����������ֵӳ�䵽������FastParaע�������
			try {
			    Annotation[] parameterAnnotations = method.getDeclaredAnnotations();
			    if (parameterAnnotations == null || parameterAnnotations.length == 0) {
			    	throw new Exception("û�ж������ע�⣡");
		        }
			    //�����ӳ����������ľ���ֵ
			    List<Object> l = new ArrayList<>();
			    
				Parameter[] parameters = method.getParameters();
				for (Parameter parameter : parameters) {
					Annotation[] annotations = parameter.getAnnotations();
					for (Annotation annotation : annotations) {
						if (annotation instanceof FastParam) {
							String value = ((FastParam) annotation).value();
							if(map.containsKey(value)){
								l.add(map.get(value));
							}
						}
					}
//					for (int i = 0; i < annotations.length; i++) {
//						for (int j = 0; j < keyArray.length; j++) {
//							if (annotations[i] instanceof FastParam) {
//								// �����ǰ�ķ������������(����˳��)��url����key���ƶ�Ӧ��add������
//								if (((FastParam) annotations[i]).value().equals(keyArray[j])) {
//									l.add(map.get(keyArray[j]));
//								}
//							}
//						}
//					}
				}	
				 
				//method.invoke(IOC.get(controllerName),l.toArray());
				//�ṩһ���ӿ� �����ʵ������ӿ� ��Ŀ�귽��ִ��ǰ����е���
				
				//�ڴ���ȥִ��Ŀ�귽��֮ǰ ��ִ��handleInterceptor������....
				//�ж�IOC�������Ƿ���handleInterceptor ���
				Collection<Object> instance = IOC.values();
				for (Object obj : instance) {
					if(obj instanceof HandleInterceptor){
						HandleInterceptor handleInterceptor = (HandleInterceptor)obj;
						//���preHandle����ִ�гɹ� ��ִ��Ŀ�귽�� 
						boolean flag = handleInterceptor.preHandle(request, response, IOC.get(controllerName).getClass());
					}
				}
				
				
				//cglib��̬����ȥִ�з��� 
				FastInterceptorCglib proxy = new FastInterceptorCglib();
				Object obj = proxy.getProxy(IOC.get(controllerName).getClass());
				method.invoke(obj,l.toArray());
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
	}
	
	public void doScanner(String packageName){
		// ���ݰ������������������
		String path = packageName.replace(".", "/");
		URL resource = this.getClass().getClassLoader().getResource(path);
		File file = new File(resource.getFile());
		for (File f : file.listFiles()) {
			//����Ǹ��ļ��еĻ� �����ݹ�ɨ��
			if (f.isDirectory()) {
				doScanner(packageName+"."+f.getName());
			}else{
				String classPath = packageName+"."+f.getName().replace(".class", "");
				classNames.add(classPath);
			}
		}
	}
	
	public String doLoadConfig(String contextConfigLocation){
		// ��ȡ��Ҫɨ��İ�
		InputStream is = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation.replace("classpath:", ""));
		if(is == null){
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
	
	public void outPutInfo(){
		for (String classname : classNames) {
			System.out.println(classname);
		}
		for (Map.Entry<String, Object> map : IOC.entrySet()) {
			System.out.println("IOC-----");
			System.out.println(map.getKey()+"-->"+map.getValue());
		}
		for (String key : handleMapping.keySet()) {
			System.out.println("handlermapping----");
			System.out.println(key+"-->"+handleMapping.get(key));
		}
	}
}
