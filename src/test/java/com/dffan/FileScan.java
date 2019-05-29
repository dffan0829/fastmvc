package com.dffan;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Test;

import com.dffan.annotation.FastController;
import com.dffan.annotation.FastMapping;

public class FileScan {
	
	private List<String> classNames = new ArrayList<>();
	private Map<String,Object> IOC = new HashMap<>();
	private Map<String,Object> handleMapping = new HashMap<>();

	
	@Test
	public void buildHandleMapping() throws DocumentException, IOException {
		doScanner(getScannPackage());
		doIOC();
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
					Method[] methods = clazz.getMethods();
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
		
		Iterator<Entry<String, Object>> iterator = handleMapping.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<String, Object> entry = iterator.next();
			System.out.println(entry.getKey()+"->11"+entry.getValue());
		}
	}
	
	public void doIOC() {
		//根据 classNames 创建对应的实例 必须加
		for (String classname : classNames) {
			System.out.println(classname);
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
				}else{
					key = clazz.getSimpleName().toLowerCase();
				}
				//创建实例对象 并且放入IOC中
				IOC.put(key, clazz.newInstance());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		for(Map.Entry<String, Object> m : IOC.entrySet()){
			System.out.println(m.getKey()+"->"+m.getValue());
		}
	}
	
	@Test
	public void testCreateInstance() throws DocumentException, IOException{
		doScanner(getScannPackage());
		//根据 classNames 创建对应的实例 必须加
		for (String classname : classNames) {
			System.out.println(classname);
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
				}else{
					key = clazz.getSimpleName().toLowerCase();
				}
				//创建实例对象 并且放入IOC中
				IOC.put(key, clazz.newInstance());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		for(Map.Entry<String, Object> m : IOC.entrySet()){
			System.out.println(m.getKey()+"->"+m.getValue());
		}
	}
	
	@Test
	public void testFile(){
		File file = new File("F:\\dffan\\workspace\\dubbo\\fastmvc\\target\\test-classes\\com\\dffan\\controller\\MyController.class");
		String name = file.getPath();
		//file.getAbsolutePath()
		System.out.println(name);
	}
	
	@Test
	public void testScan() throws DocumentException, IOException {
		doScanner(getScannPackage()) ;
	}
    
	public void doScanner(String packageName) throws DocumentException, IOException {
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

	public String getScannPackage() throws DocumentException, IOException {
		// 获取到要扫描的包
		InputStream is = this.getClass().getClassLoader().getResourceAsStream("application.xml");
		SAXReader reader = new SAXReader();
		Document doc = reader.read(is);
		is.close();
		
		Element rootElement = doc.getRootElement();
		Element element = rootElement.element("component-scan");
		Attribute attribute = element.attribute("package");
		String value = attribute.getValue();
		return value;
	}

}
