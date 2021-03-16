package com.wizzdi.flexicore.boot.remoting.service;

import com.github.ggeorgovassilis.springjsonmapper.model.UrlMapping;
import com.wizzdi.flexicore.boot.base.init.FlexiCorePluginManager;
import com.wizzdi.flexicore.boot.remoting.annotations.Remoting;
import com.wizzdi.flexicore.boot.rest.service.CustomRequestMappingHandlerMapping;
import org.pf4j.PluginWrapper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RemotingControllerCustomizer implements InitializingBean {

	@Autowired
	private FlexiCorePluginManager pluginManager;
	@Autowired
	private RequestMappingHandlerMapping requestMappingHandlerMapping;

	@Override
	public void afterPropertiesSet() throws Exception {
		loadControllers();
	}

	private void loadControllers() {

		for (PluginWrapper startedPlugin : pluginManager.getStartedPlugins()) {
			String pluginId = startedPlugin.getPluginId();
			List<Class<?>> remotingEnabledInterfaces = pluginManager.getExtensionClasses(pluginId).stream().filter(f -> f.isInterface() && AnnotationUtils.findAnnotation(f, Remoting.class) != null).collect(Collectors.toList());
			for (Class<?> remotingEnabledInterface : remotingEnabledInterfaces) {
				Object implementor = pluginManager.getApplicationContext(startedPlugin).getBean(remotingEnabledInterface);
				if(!Enhancer.isEnhanced(implementor.getClass())){
					registerController(remotingEnabledInterface, implementor);

				}


			}
		}

	}

	private void registerController(Class<?> remotingEnabledInterface, Object implementor) {
		FlexiCoreAnnotationMethodInspector flexiCoreAnnotationMethodInspector = new FlexiCoreAnnotationMethodInspector();


		CustomRequestMappingHandlerMapping requestMappingHandlerMapping = (CustomRequestMappingHandlerMapping) this.requestMappingHandlerMapping;
		for (Method method : remotingEnabledInterface.getMethods()) {
			UrlMapping requestMapping = flexiCoreAnnotationMethodInspector.inspect(method, new Object[]{});
			if (requestMapping != null) {
				RequestMappingInfo requestMappingInfo = getRequestMappingInfo(requestMapping);
				requestMappingHandlerMapping.registerMapping(requestMappingInfo, implementor, method);
			}


		}
	}


	private RequestMappingInfo getRequestMappingInfo(UrlMapping requestMapping) {
		return RequestMappingInfo
				.paths(requestMapping.getUrl())
				.methods(RequestMethod.valueOf(requestMapping.getHttpMethod().name()))
				.headers(requestMapping.getHeaders())
				.build();
	}

}
