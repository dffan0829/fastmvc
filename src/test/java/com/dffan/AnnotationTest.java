package com.dffan;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import org.junit.Test;

import com.dffan.annotation.FastParam;
import com.dffan.controller.MyController;

public class AnnotationTest {

	@Test
	public void testAnnotation() {
		Class clazz = MyController.class;
		Method[] methods = clazz.getMethods();
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
