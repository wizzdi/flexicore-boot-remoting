package com.example.pluginA.request;

public class TestRequest {

	private String name;

	public String getName() {
		return name;
	}

	public <T extends TestRequest> T setName(String name) {
		this.name = name;
		return (T) this;
	}

	@Override
	public String toString() {
		return "TestRequest{" +
				"name='" + name + '\'' +
				'}';
	}
}
