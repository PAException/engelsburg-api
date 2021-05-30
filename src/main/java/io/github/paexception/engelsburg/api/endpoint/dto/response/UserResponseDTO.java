package io.github.paexception.engelsburg.api.endpoint.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {

	private UUID userId;
	private String email;
	private String password;
	private String salt;
	private boolean verified;

}
