/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.endpoint.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeNotificationSettingsRequestDTO {

	@Schema(example = "true", defaultValue = "false")
	private boolean enabled;
	@Schema(example = "true", defaultValue = "false")
	private boolean byTimetable;

}
