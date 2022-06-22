/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.spring.auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Handles Authorization.
 *
 * <p>Usage without given value or scope enforces authorization via JWT.
 * If value or scope is given, JWT has to include these scopes to proceed.</p>
 *
 * <p>Repeatable, so many scopes can be checked.</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(Authorization.class)
public @interface AuthScope {

	String value() default "";

	String scope() default "";

}
