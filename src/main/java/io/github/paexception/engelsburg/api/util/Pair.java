/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.util;

import lombok.Data;

/**
 * Util class to handle to generics as one class.
 * Useful for mappings of streams.
 *
 * @param <A> left
 * @param <B> right
 */
@Data(staticConstructor = "of")
public class Pair<A, B> {

	private final A left;
	private final B right;

}
