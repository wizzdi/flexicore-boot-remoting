package com.wizzdi.flexicore.boot.remoting.service;

import com.github.ggeorgovassilis.springjsonmapper.model.MethodParameterDescriptor;
import com.github.ggeorgovassilis.springjsonmapper.model.UrlMapping;
import com.github.ggeorgovassilis.springjsonmapper.spring.SpringAnnotationMethodInspector;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class FlexiCoreAnnotationMethodInspector extends SpringAnnotationMethodInspector {
	@Override
	public UrlMapping inspect(Method method, Object[] objects) {

		UrlMapping urlMapping =super.inspect(method,objects);
		if(urlMapping==null){
			urlMapping=new UrlMapping();
			urlMapping.setUrl("/"+method.getDeclaringClass().getSimpleName()+"/"+method.getName());
			urlMapping.setHttpMethod(method.getParameterCount()==0?HttpMethod.GET:HttpMethod.POST);
			urlMapping.setConsumes(new String[]{MediaType.APPLICATION_JSON_VALUE});
			urlMapping.setProduces(new String[]{MediaType.APPLICATION_JSON_VALUE});

			for (int i = 0; i < method.getParameterCount(); i++) {
				Object arg=objects.length>i?objects[i]:null;
				Parameter parameter=method.getParameters()[i];
				String name = objects.length==1?"":parameter.getName();
				urlMapping.addDescriptor(new MethodParameterDescriptor(MethodParameterDescriptor.Type.requestBody, name, arg, method, i));

			}

		}
		return urlMapping;

	}
}
