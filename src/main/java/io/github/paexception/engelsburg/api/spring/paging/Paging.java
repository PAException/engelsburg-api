package io.github.paexception.engelsburg.api.spring.paging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Paging {

	private int page;
	private int size;

}
