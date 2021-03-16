package com.wizzdi.flexicore.boot.remoting.service;

import com.github.ggeorgovassilis.springjsonmapper.BaseRestInvokerProxyFactoryBean;
import com.github.ggeorgovassilis.springjsonmapper.MethodInspector;
import com.github.ggeorgovassilis.springjsonmapper.model.UrlMapping;

import java.lang.reflect.Method;

public class FlexiCoreRestInvokerProxyFactoryBean extends BaseRestInvokerProxyFactoryBean {
	@Override
	protected MethodInspector constructDefaultMethodInspector() {
		FlexiCoreAnnotationMethodInspector inspector = new FlexiCoreAnnotationMethodInspector();
		inspector.setEmbeddedValueResolver(this.expressionResolver);
		return inspector;
	}

	@Override
	public UrlMapping getRequestMapping(Method method, Object[] args) {
		return super.getRequestMapping(method, args);
	}
}
