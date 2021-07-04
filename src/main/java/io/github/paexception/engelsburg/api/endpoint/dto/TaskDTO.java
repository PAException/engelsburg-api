package io.github.paexception.engelsburg.api.endpoint.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {

	private int taskId;
	private String title;
	private long created;
	private long due;
	private String subject;
	private String content;
	private boolean done;

}
