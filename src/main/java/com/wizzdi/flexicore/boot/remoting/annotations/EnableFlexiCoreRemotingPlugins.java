package com.wizzdi.flexicore.boot.remoting.annotations;

import com.wizzdi.flexicore.boot.remoting.init.FlexiCoreRemotingModule;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import(FlexiCoreRemotingModule.class)
@Configuration
public @interface EnableFlexiCoreRemotingPlugins {
}
