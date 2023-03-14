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
public class CafeteriaInformationDTO {

	@Schema(example = "<p>Some example cafeteria content...")
	private String content;
	@Schema(example = "https://example.link")
	private String link;
	@Schema(example = "https://example.media.url/to")
	private String mediaURL;
	@Schema(example = "LEHV6nWB2yk8pyo0adR*.7kCMdnj")
	private String blurHash;

}
