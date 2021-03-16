package com.wizzdi.flexicore.boot.remoting.service;

import com.github.ggeorgovassilis.springjsonmapper.utils.ProxyFactory;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class CGLibProxyFactory implements ProxyFactory {
	private Class<?> baseClass=Object.class;
	protected ClassLoader classLoader;
	@Override
	public Object createProxy(ClassLoader classLoader, Class<?>[] interfaces, InvocationHandler callback) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(this.baseClass);
		if (classLoader == null) {
			classLoader = Thread.currentThread().getContextClassLoader();
		}

		enhancer.setClassLoader(classLoader);
		enhancer.setCallback((MethodInterceptor)(obj, method, args, proxy)-> callback.invoke(obj, method, args));

		enhancer.setInterfaces(interfaces);
		return enhancer.create();
	}

	@Override
	public void setProxyTargetClass(Class<?> aClass) {
		this.baseClass=aClass;
	}

	@Override
	public void setProxyTargetClassLoader(ClassLoader classLoader) {
		this.classLoader=classLoader;
	}
}
