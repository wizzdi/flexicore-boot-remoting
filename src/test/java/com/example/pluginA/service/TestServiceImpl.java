package com.example.pluginA.service;

import com.example.pluginA.request.TestRequest;
import com.example.pluginA.response.TestResponse;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;

@Component
@Extension
public class TestServiceImpl implements TestService{

	@Override
	public TestResponse test(TestRequest testRequest,TestRequest testRequest2) {
		return new TestResponse().setName(testRequest.getName()+" greeted");
	}
}
