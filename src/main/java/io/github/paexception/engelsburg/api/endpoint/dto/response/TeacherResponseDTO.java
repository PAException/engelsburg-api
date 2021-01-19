package io.github.paexception.engelsburg.api.endpoint.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeacherResponseDTO {

	private String abbreviation;
	private String firstname;
	private String surname;
	private String gender;
	private boolean mentionedPhD;
	private List<String> jobs;

}
