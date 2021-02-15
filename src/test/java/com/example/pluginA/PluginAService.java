package com.example.pluginA;

import com.example.pluginA.request.TestRequest;
import com.example.pluginA.response.TestResponse;
import com.example.pluginA.service.TestService;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Extension
@RestController
public class PluginAService implements Plugin {

	@Autowired
	private TestService testService;

	@PostMapping("/test")
	public TestResponse test(@RequestBody TestRequest testRequest) {
		return testService.test(testRequest);
	}


}
