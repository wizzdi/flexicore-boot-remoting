package com.wizzdi.flexicore.boot.remoting.service;

import com.github.ggeorgovassilis.springjsonmapper.model.UrlMapping;
import com.wizzdi.flexicore.boot.base.init.FlexiCoreApplicationContext;
import com.wizzdi.flexicore.boot.base.interfaces.ContextCustomizer;
import com.wizzdi.flexicore.boot.remoting.annotations.Remoting;
import com.wizzdi.flexicore.boot.rest.service.CustomRequestMappingHandlerMapping;
import org.apache.commons.lang.StringUtils;
import org.pf4j.PluginManager;
import org.pf4j.PluginWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class RemotingCustomizer implements ContextCustomizer {

	@Autowired
	@LoadBalanced
	private RestTemplate restTemplate;
	



	@Override
	public void customize(FlexiCoreApplicationContext applicationContext, PluginWrapper pluginWrapper, PluginManager pluginManager) {
		String pluginId = pluginWrapper.getPluginId();
		List<Class<?>> remotingEnabledInterfaces = pluginManager.getExtensionClasses(pluginId).stream().filter(f -> f.isInterface() && AnnotationUtils.findAnnotation(f, Remoting.class) != null).collect(Collectors.toList());
		for (Class<?> remotingEnabledInterface : remotingEnabledInterfaces) {
			Remoting annotation = AnnotationUtils.findAnnotation(remotingEnabledInterface, Remoting.class);
			if (annotation != null) {
				List<?> implementors = pluginManager.getExtensionClasses(remotingEnabledInterface, pluginId).stream().filter(f->!f.isInterface()).collect(Collectors.toList());
				if (implementors.isEmpty()) {
					registerRemote(applicationContext, remotingEnabledInterface,annotation);
				}
			}

		}
	}

	private  void registerRemote(FlexiCoreApplicationContext applicationContext, Class<?> remotingEnabledInterface, Remoting annotation) {
		String beanName = StringUtils.uncapitalize(remotingEnabledInterface.getSimpleName());
		FlexiCoreRestInvokerProxyFactoryBean remote = createRemote(remotingEnabledInterface, annotation);
		applicationContext.getAutowireCapableBeanFactory().registerSingleton(beanName, remote);
	}

	private FlexiCoreRestInvokerProxyFactoryBean createRemote(Class<?> remotingEnabledInterface, Remoting annotation) {
		FlexiCoreRestInvokerProxyFactoryBean proxyFactory = new FlexiCoreRestInvokerProxyFactoryBean();
		String serviceName=annotation.discoveryServiceName().isEmpty()?remotingEnabledInterface.getSimpleName():annotation.discoveryServiceName();
		proxyFactory.setBaseUrl("http://"+serviceName);
		proxyFactory.setRemoteServiceInterfaceClass(remotingEnabledInterface);
		proxyFactory.setRestTemplate(restTemplate);
		CGLibProxyFactory cgLibProxyFactory = new CGLibProxyFactory();
		cgLibProxyFactory.setProxyTargetClass(remotingEnabledInterface);
		cgLibProxyFactory.setProxyTargetClassLoader(remotingEnabledInterface.getClassLoader());
		proxyFactory.setProxyFactory(cgLibProxyFactory);
		proxyFactory.initialize();
		return proxyFactory;
	}





	@LoadBalanced
	@Bean
	@ConditionalOnMissingBean(value = RestTemplate.class,annotation = LoadBalanced.class)
	RestTemplate restTemplate() {
		return new RestTemplate();
	}

}
