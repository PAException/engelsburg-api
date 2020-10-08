package io.github.paexception.engelsburg.api.endpoint.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateEventRequestDTO {

	private Date date;
	private String title;

}
