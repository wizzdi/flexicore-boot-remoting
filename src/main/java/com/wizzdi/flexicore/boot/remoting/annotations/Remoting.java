package com.wizzdi.flexicore.boot.remoting.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Remoting {
	String discoveryServiceName() default "";
	boolean generateController() default true;
}
