package io.github.paexception.engelsburg.api.endpoint.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {

	private Date date;
	private String title;

}
