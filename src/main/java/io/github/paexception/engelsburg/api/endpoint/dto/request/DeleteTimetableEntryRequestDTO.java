package io.github.paexception.engelsburg.api.endpoint.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteTimetableEntryRequestDTO {

	@Range(min = 0, max = 4)//MON to FRI
	private int day = -1;
	@Range(min = 0, max = 10)//1 to 11 lesson
	private int lesson = -1;

}
