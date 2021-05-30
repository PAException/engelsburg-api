package io.github.paexception.engelsburg.api.authorization.interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(Authorization.class)
public @interface AuthScope {

	String value() default "";

	String scope() default "";

}
