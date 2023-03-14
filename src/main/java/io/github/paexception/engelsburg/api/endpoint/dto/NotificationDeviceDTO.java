/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.endpoint.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDeviceDTO {

	@NotBlank //Because of endpoint use
	@Schema(example = "some_example_device_token")
	private String token;
	@NotBlank
	@Schema(example = "de_DE")
	private String langCode;

}
