package io.github.paexception.engelsburg.api.controller;

import io.github.paexception.engelsburg.api.endpoint.dto.response.GetClassesResponseDTO;
import io.github.paexception.engelsburg.api.util.Error;
import io.github.paexception.engelsburg.api.util.Result;
import org.springframework.stereotype.Component;

@Component
public class InformationController {

	private static String[] currentClasses;

	public void setCurrentClasses(String[] classes) {
		currentClasses = classes;
	}

	public Result<GetClassesResponseDTO> getCurrentClasses() {
		if (currentClasses.length==0) return Result.of(Error.NOT_FOUND, "info_classes");

		return Result.of(new GetClassesResponseDTO(currentClasses));
	}

}
