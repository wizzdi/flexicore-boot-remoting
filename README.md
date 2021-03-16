 



# ![](https://support.wizzdi.com/wp-content/uploads/2020/05/flexicore-boot-remoting-icon-extra-small.png) Flexicore-boot-remoting [![Build Status](https://jenkins.wizzdi.com/buildStatus/icon?job=Flexicore-boot-remoting)](https://jenkins.wizzdi.com/job/Flexicore-boot-remoting/)[![Maven Central](https://img.shields.io/maven-central/v/com.wizzdi/flexicore-boot-remoting-api.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.wizzdi%22%20AND%20a:%22flexicore-boot-remoting%22)[![Maven Central](https://img.shields.io/docker/cloud/automated/wizzdi/flexicore-boot-remoting)](https://hub.docker.com/r/wizzdi/flexicore-boot-remoting)


For comprehensive information about flexicore-boot-remoting please visit our [site](http://wizzdi.com/).

## What it does?

FlexiCore Boot Remoting is a FlexiCore Module that allows moving service implementations between running environments, if a local implementation will be found it will be called ,
otherwise using discovery a remote implementation of the service will be called

## How to use?
Add the flexicore-boot-remoting dependency using the latest version available from maven central:

            <dependency>
                <groupId>com.wizzdi</groupId>
                <artifactId>flexicore-boot-remoting</artifactId>
                <version>LATEST</version>
            </dependency>
Simply annotate your application class or your configuration class with

    @EnableFlexiCoreRemotingPlugins

## Example
your application class:

    @EnableFlexiCorePlugins  
    @EnableFlexiCoreRemotingPlugins
    @SpringBootApplication  
    public class App {  
      
       public static void main(String[] args) {  
      
          SpringApplication app = new SpringApplication(App.class);  
      app.addListeners(new ApplicationPidFileWriter());  
      ConfigurableApplicationContext context=app.run(args);  
      
      }
    }
an interface annotated inside an interface-plugin plugin:

    @Extension
    @Remoting
    public interface TestService extends Plugin {

	    TestResponse test(TestRequest testRequest,TestRequest testRequest2);
    }

an implementation inside an interface-impl-plugin plugin:

    @Component
    @Extension
    public class TestServiceImpl implements TestService{

	    @Override
	    public TestResponse test(TestRequest testRequest,TestRequest testRequest2) {
		    return new TestResponse().setName(testRequest.getName()+" greeted");
	    }
    }


## Main Dependencies

[FlexiCore Boot](https://github.com/wizzdi/flexicore-boot)

[Spring Boot Starter Web](https://search.maven.org/artifact/org.springframework.boot/spring-boot-starter-web)

[Spring REST Invoker](https://search.maven.org/artifact/com.github.ggeorgovassilis/spring-rest-invoker)