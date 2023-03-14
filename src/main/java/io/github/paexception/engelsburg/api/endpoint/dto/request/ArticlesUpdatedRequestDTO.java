/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.endpoint.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticlesUpdatedRequestDTO {

	@Schema(example = "[\"8d635bcd25dd181aa75ff3812f1e0f6e20192c04\"]")
	@Size(min = 1)
	private List<String> hashes;

}
