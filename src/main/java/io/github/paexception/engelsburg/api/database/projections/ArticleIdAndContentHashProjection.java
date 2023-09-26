/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.database.projections;

public interface ArticleIdAndContentHashProjection {

	int getArticleId();

	String getContentHash();
}
