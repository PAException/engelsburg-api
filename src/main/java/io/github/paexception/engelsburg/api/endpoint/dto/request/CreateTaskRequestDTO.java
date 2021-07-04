package io.github.paexception.engelsburg.api.endpoint.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTaskRequestDTO {

	@NotBlank
	private String title;
	private long created = -1;
	private long due = -1;
	private String subject;
	private String content;

}
