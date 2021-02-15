package com.example.pluginA.response;

public class TestResponse {

	private String name;

	public String getName() {
		return name;
	}

	public <T extends TestResponse> T setName(String name) {
		this.name = name;
		return (T) this;
	}

	@Override
	public String toString() {
		return "TestResponse{" +
				"name='" + name + '\'' +
				'}';
	}
}
