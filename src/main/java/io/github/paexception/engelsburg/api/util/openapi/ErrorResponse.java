/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.util.openapi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(ErrorResponses.class)
public @interface ErrorResponse {

	int status();

	String messageKey();

	String extra() default "";

	/**
	 * Used to identify errors with same status. (400 - Bad Request, 400 - Failed)
	 * @return key
	 */
	String key() default "";

	String description() default "";
}
