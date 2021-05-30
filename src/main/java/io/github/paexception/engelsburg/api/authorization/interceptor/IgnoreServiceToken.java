package io.github.paexception.engelsburg.api.authorization.interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * IgnoreServiceToken annotation for classes and functions to not be checked for a correct ServiceToken
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface IgnoreServiceToken {
}
