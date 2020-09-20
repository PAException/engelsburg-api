package io.github.paexception.engelsburg.api.controller;

import io.github.paexception.engelsburg.api.endpoint.dto.response.GetClassesResponseDTO;
import io.github.paexception.engelsburg.api.util.Result;
import org.springframework.stereotype.Component;

@Component
public class InformationController {

	private static String[] currentClasses;

	public void setCurrentClasses(String[] classes) {
		currentClasses = classes;
	}

	public Result<GetClassesResponseDTO> getCurrentClasses() {
		return Result.of(new GetClassesResponseDTO(currentClasses));
	}

}
