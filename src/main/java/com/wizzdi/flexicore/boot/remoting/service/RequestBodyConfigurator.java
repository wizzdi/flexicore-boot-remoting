package com.wizzdi.flexicore.boot.remoting.service;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import java.util.List;

@Configuration
public class RequestBodyConfigurator {


	@Bean
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public RequestResponseBodyMethodProcessor requestResponseBodyMethodProcessor(RequestMappingHandlerAdapter requestMappingHandlerAdapter) {
		List<HttpMessageConverter<?>> messageConverters = requestMappingHandlerAdapter.getMessageConverters();
		return new RequestResponseBodyMethodProcessor(messageConverters);

	}
}
