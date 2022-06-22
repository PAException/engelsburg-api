/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.endpoint.dto;


import io.github.paexception.engelsburg.api.util.Error;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class ErrorDTO {

	@Schema(example = "404")
	private int status;
	@Schema(example = "NOT_FOUND")
	private String messageKey;
	@Schema(example = "article")
	private String extra;

	public boolean isError(Error error) {
		return this.status == error.getStatus()
				&& Objects.equals(this.messageKey, error.getMessageKey())
				&& Objects.equals(this.extra, error.getExtra());
	}
}
