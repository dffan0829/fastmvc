package com.dffan;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.TreeMap;

import org.junit.Test;

import com.dffan.annotation.FastParam;
import com.dffan.controller.MyController;

public class AnnotationTest {


	@Test
	public void testTree() {
		
		
	}
	
	@Test
	public void testAnnotation() {
		Class clazz = MyController.class;
		Method[] methods = clazz.getDeclaredMethods();
		for (Method method : methods) {
			/*String name = method.getName();
			if ("getInfo".equals(name)) {
				Annotation[][] parameterAnnotations = method.getParameterAnnotations();
				for (Annotation[] annotations : parameterAnnotations) {
					for (Annotation annotation : annotations) {
						if (annotation instanceof FastParam) {
							String value = ((FastParam) annotation).value();
							System.out.println(value);
						}
					}
				}
			}*/

			Parameter[] parameters = method.getParameters();
			Class<?>[] parameterTypes = method.getParameterTypes();
			for (Class<?> class1 : parameterTypes) {
				String name = class1.getName();
				System.out.println(name);
			}
			for (Parameter parameter : parameters) {
				Annotation[] annotations = parameter.getAnnotations();
				for (Annotation annotation : annotations) {
					if (annotation instanceof FastParam) {
						String value = ((FastParam) annotation).value();
						System.out.println(value+","+parameter.getName());
					}
				}
			}

		}
	}

}
