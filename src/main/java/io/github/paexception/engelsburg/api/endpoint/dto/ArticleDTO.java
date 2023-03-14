/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.endpoint.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleDTO {

	@Schema(example = "108")
	private int articleId;
	@Schema(example = "1645214659909")
	private long date;
	@Schema(example = "https://any.link/to")
	private String link;
	@Schema(example = "Interesting title")
	private String title;
	@Schema(example = "<p>About interesting articles...")
	private String content;
	@Schema(example = "8d635bcd25dd181aa75ff3812f1e0f6e20192c04")
	private String contentHash;
	@Schema(example = "https://some.media.url/to")
	private String mediaUrl;
	@Schema(example = "LEHV6nWB2yk8pyo0adR*.7kCMdnj")
	private String blurHash;

}
