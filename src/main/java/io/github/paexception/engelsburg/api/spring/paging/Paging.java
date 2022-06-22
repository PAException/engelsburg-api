/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.spring.paging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Simple paging data containing page and size of page info.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Paging {

	private int page;
	private int size;

}
