package com.wizzdi.flexicore.boot.remoting.service;

import com.netflix.discovery.EurekaClient;

import java.util.List;

public class EurekaClientHolder {

	private final List<EurekaClient> eurekaClients;

	public EurekaClientHolder(List<EurekaClient> eurekaClients) {
		this.eurekaClients = eurekaClients;
	}

	public List<EurekaClient> getEurekaClients() {
		return eurekaClients;
	}

	public void shutdown(){
		for (EurekaClient eurekaClient : eurekaClients) {
			eurekaClient.shutdown();
		}
	}
}
