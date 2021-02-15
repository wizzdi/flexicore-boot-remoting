package com.wizzdi.flexicore.boot.remoting.service;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.EurekaInstanceConfig;
import com.netflix.appinfo.HealthCheckHandler;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.AbstractDiscoveryClientOptionalArgs;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.EurekaClientConfig;
import com.wizzdi.flexicore.boot.base.init.FlexiCorePluginManager;
import com.wizzdi.flexicore.boot.remoting.annotations.Remoting;
import com.wizzdi.flexicore.boot.rest.service.CustomRequestMappingHandlerMapping;
import org.pf4j.PluginLoader;
import org.pf4j.PluginManager;
import org.pf4j.PluginWrapper;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.CommonsClientAutoConfiguration;
import org.springframework.cloud.client.ConditionalOnDiscoveryEnabled;
import org.springframework.cloud.client.serviceregistry.ServiceRegistryAutoConfiguration;
import org.springframework.cloud.netflix.eureka.CloudEurekaClient;
import org.springframework.cloud.util.ProxyUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties
@ConditionalOnClass(EurekaClientConfig.class)
@ConditionalOnProperty(value = "eureka.client.enabled", matchIfMissing = true)
@ConditionalOnDiscoveryEnabled
@AutoConfigureBefore({ CommonsClientAutoConfiguration.class, ServiceRegistryAutoConfiguration.class })
@AutoConfigureAfter(name = { "org.springframework.cloud.netflix.eureka.config.DiscoveryClientOptionalArgsConfiguration",
		"org.springframework.cloud.autoconfigure.RefreshAutoConfiguration",
		"org.springframework.cloud.netflix.eureka.EurekaDiscoveryClientConfiguration",
		"org.springframework.cloud.client.serviceregistry.AutoServiceRegistrationAutoConfiguration" })
public class ServiceConfiguration  {


	@Autowired
	private ApplicationContext context;

	@Autowired
	private AbstractDiscoveryClientOptionalArgs<?> optionalArgs;


	@Bean(destroyMethod = "shutdown")
	@org.springframework.cloud.context.config.annotation.RefreshScope
	public EurekaClientHolder eurekaClients(EurekaClientConfig config,
											EurekaInstanceConfig instance, @Autowired(required = false) HealthCheckHandler healthCheckHandler, FlexiCorePluginManager pluginManager) {
		// If we use the proxy of the ApplicationInfoManager we could run into a
		// problem
		// when shutdown is called on the CloudEurekaClient where the
		// ApplicationInfoManager bean is
		// requested but wont be allowed because we are shutting down. To avoid this
		// we use the
		// object directly.
		Set<String> serviceNames=new HashSet<>();
		for (PluginWrapper startedPlugin : pluginManager.getStartedPlugins()) {
			List<Class<?>> extensions=pluginManager.getExtensionClasses(startedPlugin.getPluginId()).stream().filter(f-> AnnotationUtils.findAnnotation(f,Remoting.class)!=null&&!pluginManager.getExtensions(f,startedPlugin.getPluginId()).isEmpty()).collect(Collectors.toList());
			serviceNames.addAll(extensions.stream().map(f->getServiceName(f)).collect(Collectors.toSet()));
		}
		List<EurekaClient> clients=new ArrayList<>();
		for (String serviceName : serviceNames) {
			ApplicationInfoManager manager=eurekaApplicationInfoManager(instance,serviceName);
			ApplicationInfoManager appManager;
			if (AopUtils.isAopProxy(manager)) {
				appManager = ProxyUtils.getTargetObject(manager);
			}
			else {
				appManager = manager;
			}
			CloudEurekaClient cloudEurekaClient = new CloudEurekaClient(appManager, config, this.optionalArgs,
					this.context);
			cloudEurekaClient.registerHealthCheck(healthCheckHandler);
		}

		return new EurekaClientHolder(clients);
	}

	private String getServiceName(Class<?> remotingInterface) {
		Remoting annotation = AnnotationUtils.findAnnotation(remotingInterface, Remoting.class);
		return annotation==null||annotation.discoveryServiceName().isEmpty()?remotingInterface.getSimpleName():annotation.discoveryServiceName();
	}


	public ApplicationInfoManager eurekaApplicationInfoManager(EurekaInstanceConfig config, String serviceName) {
		String instanceId=config.getInstanceId().replace(config.getAppname(),serviceName);
		InstanceInfo instanceInfo = new InstanceInfoFactoryMod().create(config)
				.setInstanceId(instanceId).setAppName(serviceName).setVIPAddress(serviceName).setSecureVIPAddress(serviceName).setStatus(InstanceInfo.InstanceStatus.UP).build();
		return new ApplicationInfoManager(config, instanceInfo);
	}


}
