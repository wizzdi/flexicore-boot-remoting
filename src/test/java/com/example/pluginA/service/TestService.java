package com.example.pluginA.service;


import com.example.pluginA.request.TestRequest;
import com.example.pluginA.response.TestResponse;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.boot.remoting.annotations.Remoting;
import org.pf4j.Extension;

@Extension
@Remoting
public interface TestService extends Plugin {

	TestResponse test(TestRequest testRequest,TestRequest testRequest2);
}
