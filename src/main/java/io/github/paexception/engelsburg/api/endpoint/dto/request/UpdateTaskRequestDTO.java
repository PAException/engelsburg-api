package io.github.paexception.engelsburg.api.endpoint.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTaskRequestDTO {

	private int taskId = -1;
	private String title;
	private long due = -1;
	private String subject;
	private String content;

}
