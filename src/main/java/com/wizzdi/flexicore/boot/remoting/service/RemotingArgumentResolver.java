package com.wizzdi.flexicore.boot.remoting.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wizzdi.flexicore.boot.remoting.annotations.Remoting;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.Method;


@Configuration
public class RemotingArgumentResolver implements HandlerMethodArgumentResolver, ApplicationContextAware {

	private static ObjectMapper objectMapper;


	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		if (AnnotationUtils.findAnnotation(parameter.getMember().getDeclaringClass(), Remoting.class) == null) {
			return false;
		}
		return parameter.getParameterAnnotations().length == 0;
	}

	@Override
	public Object resolveArgument(
			MethodParameter parameter,
			ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest,
			WebDataBinderFactory binderFactory) throws Exception {

		Method method = parameter.getMethod();
		if (mavContainer == null || method == null) {
			return null;
		}

      JsonNode o = (JsonNode) mavContainer.getModel().getAttribute("");
      if(o==null){
        HttpServletRequest nativeRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        if(nativeRequest!=null){
          o=objectMapper.readTree(nativeRequest.getInputStream());
          mavContainer.getModel().addAttribute("",o);
        }

      }

      if(o!=null){
        if(parameter.getMethod().getParameterCount()==1){
          return objectMapper.treeToValue(o,parameter.getParameterType());
        }
        else{
          JsonNode path = o.path(parameter.getParameter().getName());
          return objectMapper.treeToValue(path,parameter.getParameterType());

        }
      }
      return o;

	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
      objectMapper = applicationContext.getBean(ObjectMapper.class);
	}
}