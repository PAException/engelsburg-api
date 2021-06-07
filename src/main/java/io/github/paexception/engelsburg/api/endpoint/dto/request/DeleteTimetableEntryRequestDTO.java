package io.github.paexception.engelsburg.api.endpoint.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteTimetableEntryRequestDTO {

	private int day;
	private int lesson;

}
