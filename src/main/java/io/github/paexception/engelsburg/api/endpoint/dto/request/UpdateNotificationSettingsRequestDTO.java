/*
 * Copyright (c) 2023 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.endpoint.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateNotificationSettingsRequestDTO {

	@Schema(example = "<fcm token>")
	@NotBlank
	private String token;
	@Schema(example = "[\"substitute.1.2.GRB.10c\", \"substitute.3.1.GAR.9b\"]")
	@Size(min = 1)
	private List<String> priorityTopics;
}
