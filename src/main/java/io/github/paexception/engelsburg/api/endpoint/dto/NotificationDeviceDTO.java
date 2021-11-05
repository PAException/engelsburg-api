package io.github.paexception.engelsburg.api.endpoint.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDeviceDTO {

	@NotBlank //Because of endpoint use
	private String token;

}
