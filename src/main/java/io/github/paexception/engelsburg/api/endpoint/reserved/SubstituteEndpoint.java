package io.github.paexception.engelsburg.api.endpoint.reserved;

import io.github.paexception.engelsburg.api.controller.reserved.SubstituteController;
import io.github.paexception.engelsburg.api.endpoint.dto.UserDTO;
import io.github.paexception.engelsburg.api.spring.auth.AuthScope;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.constraints.NotBlank;

/**
 * RestController for substitute actions.
 */
@Validated
@RestController
public class SubstituteEndpoint {

	private final SubstituteController substituteController;

	public SubstituteEndpoint(
			SubstituteController substituteController) {
		this.substituteController = substituteController;
	}

	/**
	 * Get all substitutes since specific date.
	 *
	 * @param date can't be in the past
	 * @return found substitutes
	 */
	@AuthScope("substitute.read.current")
	@GetMapping("/substitute")
	public Object getAllSubstitutes(@RequestParam(required = false, defaultValue = "-1") long date, UserDTO userDTO) {
		return this.substituteController.getAllSubstitutes(date, userDTO).getHttpResponse();
	}

	/**
	 * <b>Just returns all substitutes of the day and future</b>.
	 *
	 * @see SubstituteController#getSubstitutesByClassName(String, long, UserDTO)
	 */
	@AuthScope("substitute.read.current")
	@GetMapping("/substitute/className")
	public Object getSubstitutesByClassName(@RequestParam @NotBlank @Length(min = 2, max = 3) String className,
			@RequestParam(required = false, defaultValue = "-1") long date,
			UserDTO userDTO) {
		return this.substituteController.getSubstitutesByClassName(className, date, userDTO).getHttpResponse();
	}

	/**
	 * Get all substitutes based on the teacher.
	 *
	 * @see SubstituteController#getSubstitutesByTeacher(String, int, String, long, UserDTO)
	 */
	@AuthScope("substitute.read.current")
	@GetMapping("/substitute/teacher")
	public Object getSubstitutesByTeacher(@RequestParam @NotBlank String teacher,
			@RequestParam(required = false, defaultValue = "-1") int lesson,
			@RequestParam(required = false) String className,
			@RequestParam(required = false, defaultValue = "-1") long date, UserDTO userDTO) {
		return this.substituteController.getSubstitutesByTeacher(teacher, lesson, className, date,
				userDTO).getHttpResponse();
	}

	/**
	 * Get all substitutes based on the substitute teacher.
	 *
	 * @see SubstituteController#getSubstitutesBySubstituteTeacher(String, long, UserDTO)
	 */
	@AuthScope("substitute.read.current")
	@GetMapping("/substitute/substituteTeacher")
	public Object getSubstitutesBySubstituteTeacher(@RequestParam @NotBlank String teacher,
			@RequestParam(required = false, defaultValue = "-1") long date,
			UserDTO userDTO) {
		return this.substituteController.getSubstitutesBySubstituteTeacher(teacher, date, userDTO).getHttpResponse();
	}

}
