/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.endpoint.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {

	@Schema(example = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJiMDE4NTQyNS04MDRjLTRhYTYtYWE2OC00NDhjZjQ1MDAwZmUiLCJpc3MiOiJlbmdlbHNidXJnLWFwaSIsInNjb3BlcyI6ImluZm8uY2xhc3Nlcy5yZWFkLmFsbC0tdGVhY2hlci5yZWFkLmFsbC0tLW5vdGlmaWNhdGlvbi5zZXR0aW5ncy5yZWFkLnNlbGYtd3JpdGUuc2VsZi0tLXN1YnN0aXR1dGUubWVzc2FnZS5yZWFkLmN1cnJlbnQtLXJlYWQuY3VycmVudC0tdXNlci5kYXRhLmRlbGV0ZS5zZWxmLXJlYWQuc2VsZiIsImV4cCI6MTY0NTQ2NjM0MiwiaWF0IjoxNjQ1NDY2MDQyfQ.qSayTfpQtIi94qEP9Ud8dZManoWI-LGmHa2eFyY2-apyklbTqOU3LyJ4sVHdK3svxgWwvoE83ptGYPnI7txrvw")
	private String token;
	@Schema(example = "vUt1a1SiqDOnjCt5NqiflIgCGrOUTR5xXqdbWhh4fXHHKUoqW5bQiw5UGNCH1NbxyguI8rrgK09Me9LSS341NBmOMSsBgThpJFeu")
	private String refreshToken;
	@Schema(example = "any.email@gmail.com")
	private String email;
	@Schema(example = "Max Mustermann")
	private String username;
	@Schema(example = "10c")
	private String className;
	@Schema(example = "[\"email\"]")
	private String[] loginVia;
	@Schema(example = "true")
	private boolean verified;

}
