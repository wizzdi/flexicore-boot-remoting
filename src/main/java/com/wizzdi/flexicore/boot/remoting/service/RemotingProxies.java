package com.wizzdi.flexicore.boot.remoting.service;

import java.util.Set;

public class RemotingProxies {
	
	private final Set<Class<?>> proxiedInterfaces;

	public RemotingProxies(Set<Class<?>> proxiedInterfaces) {
		this.proxiedInterfaces = proxiedInterfaces;
	}

	public Set<Class<?>> getProxiedInterfaces() {
		return proxiedInterfaces;
	}
}
