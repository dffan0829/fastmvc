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
	
	//定义一个List存放所有的扫描包下面的classname 便于后期创建实例
	private static List<String> classNames = new ArrayList<>();
	//定义一个IOC容器用于将控制器与实例对象建立对应关系
	private static Map<String,Object> IOC = new HashMap<>();
	//创建handlemapping映射关系 将fastmapping与目标方法建立映射关系
	private static Map<String,Object> handleMapping = new HashMap<>();

	
	//框架初始化的入口方法 先扫描指定的包下的所有类 拿到对应的注解信息 将注解信息中的值与目标处理器的方法做一个映射 
	@Override
	public void init(ServletConfig config) {
		String contextConfigLocation = config.getInitParameter("contextConfigLocation");
		//加载配置文件 获取到配置文件中的值 自动扫描的报名
		String packageName = doLoadConfig(contextConfigLocation);
		//扫描指定包下的所有类
		doScanner(packageName);
		//创建IOC容器
		createIOC();
		//创建handlemapping映射关系 将fastmapping与目标方法建立映射关系
		buildHandleMapping();
		
		//outPutInfo();
	
	}

	public void buildHandleMapping() {
		//ioc容器为空时候 退出
		if(IOC == null){
			return ;
		}
		for (String className : classNames) {
			try {
				Class<?> clazz = Class.forName(className);
				//如果当前类没有该注解的话 跳出
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
		//根据 classNames 创建对应的实例 必须加
		for (String classname : classNames) {
			try {
				Class<?> clazz = Class.forName(classname);
				String key = "";
				  //判断类上是否有控制器的注解
				if(clazz.isAnnotationPresent(FastController.class)){
					String controllerName = clazz.getDeclaredAnnotation(FastController.class).value();
					if(controllerName!= null && !controllerName.isEmpty()){
						key = controllerName.toLowerCase();
					}else{
						key = clazz.getSimpleName().toLowerCase();
					}
				}else if(clazz.isAnnotationPresent(FastComponent.class)){
					//判断类上是否有组件的注解
					String componentName = clazz.getDeclaredAnnotation(FastComponent.class).value();
					if(componentName!= null && !componentName.isEmpty()){
						key = componentName.toLowerCase();
					}else{
						key = clazz.getSimpleName().toLowerCase();
					}
				} 
				//创建实例对象 并且放入IOC中
				IOC.put(key, clazz.newInstance());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String url = request.getRequestURI().replace(request.getContextPath(), "");
		//判断是否请求被映射过 没有有的 交给servlet处理
		if(!handleMapping.containsKey(url)){
			super.service(request, response);
		}else{
			//获取到所有的请求参数
			Map<String,Object> map = new LinkedHashMap<>();
			Enumeration<String> parameterNames = request.getParameterNames();
			if(parameterNames !=null){
				while(parameterNames.hasMoreElements()){
					String paraName = parameterNames.nextElement();
					String paraVal = request.getParameter(paraName);
					map.put(paraName, paraVal);
				}
			}
			//将所有请求参数的key放入数组
			Object[] keyArray = map.keySet().toArray();
			
			Method method = (Method) handleMapping.get(url);
			String controllerName = method.getDeclaringClass().getAnnotation(FastController.class).value().toLowerCase();
			//将请求参数的值映射到方法的FastPara注解参数上
			try {
			    Annotation[] parameterAnnotations = method.getDeclaredAnnotations();
			    if (parameterAnnotations == null || parameterAnnotations.length == 0) {
			    	throw new Exception("没有定义参数注解！");
		        }
			    //存放着映射请求参数的具体值
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
//								// 如果当前的方法的入参名称(按照顺序)和url参数key名称对应则add进集合
//								if (((FastParam) annotations[i]).value().equals(keyArray[j])) {
//									l.add(map.get(keyArray[j]));
//								}
//							}
//						}
//					}
				}	
				 
				//method.invoke(IOC.get(controllerName),l.toArray());
				//提供一个接口 类可以实现这个接口 在目标方法执行前后进行调用
				
				//在代理去执行目标方法之前 先执行handleInterceptor拦截器....
				//判断IOC容器中是否有handleInterceptor 组件
				Collection<Object> instance = IOC.values();
				for (Object obj : instance) {
					if(obj instanceof HandleInterceptor){
						HandleInterceptor handleInterceptor = (HandleInterceptor)obj;
						//如果preHandle方法执行成功 在执行目标方法 
						boolean flag = handleInterceptor.preHandle(request, response, IOC.get(controllerName).getClass());
					}
				}
				
				
				//cglib动态代理去执行方法 
				FastInterceptorCglib proxy = new FastInterceptorCglib();
				Object obj = proxy.getProxy(IOC.get(controllerName).getClass());
				method.invoke(obj,l.toArray());
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
	}
	
	public void doScanner(String packageName){
		// 根据包名查找下面的所有类
		String path = packageName.replace(".", "/");
		URL resource = this.getClass().getClassLoader().getResource(path);
		File file = new File(resource.getFile());
		for (File f : file.listFiles()) {
			//如果是个文件夹的话 继续递归扫描
			if (f.isDirectory()) {
				doScanner(packageName+"."+f.getName());
			}else{
				String classPath = packageName+"."+f.getName().replace(".class", "");
				classNames.add(classPath);
			}
		}
	}
	
	public String doLoadConfig(String contextConfigLocation){
		// 获取到要扫描的包
		InputStream is = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation.replace("classpath:", ""));
		if(is == null){
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
