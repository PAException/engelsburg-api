package io.github.paexception.engelsburg.api.endpoint.reserved;

import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.paexception.engelsburg.api.controller.reserved.SubstituteController;
import io.github.paexception.engelsburg.api.spring.auth.AuthScope;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
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

	@Autowired
	private SubstituteController substituteController;

	/**
	 * Get all substitutes since specific date.
	 *
	 * @param date can't be in the past
	 * @return found substitutes
	 */
	@AuthScope("substitute.read.current")
	@GetMapping("/substitute")
	public Object getAllSubstitutes(@RequestParam(required = false, defaultValue = "-1") long date, DecodedJWT jwt) {
		return this.substituteController.getAllSubstitutes(date, jwt).getHttpResponse();
	}

	/**
	 * <b>Just returns all substitutes of the day and future</b>.
	 *
	 * @see SubstituteController#getSubstitutesByClassName(String, long, DecodedJWT)
	 */
	@AuthScope("substitute.read.current")
	@GetMapping("/substitute/className")
	public Object getSubstitutesByClassName(@RequestParam @NotBlank @Length(min = 2, max = 3) String className,
											@RequestParam(required = false, defaultValue = "-1") long date,
											DecodedJWT jwt) {
		return this.substituteController.getSubstitutesByClassName(className, date, jwt).getHttpResponse();
	}

	/**
	 * Get all substitutes based on the teacher.
	 *
	 * @see SubstituteController#getSubstitutesByTeacher(String, int, String, long, DecodedJWT)
	 */
	@AuthScope("substitute.read.current")
	@GetMapping("/substitute/teacher")
	public Object getSubstitutesByTeacher(@RequestParam @NotBlank String teacher,
										  @RequestParam(required = false, defaultValue = "-1") int lesson,
										  @RequestParam(required = false) String className,
										  @RequestParam(required = false, defaultValue = "-1") long date, DecodedJWT jwt) {
		return this.substituteController.getSubstitutesByTeacher(teacher, lesson, className, date, jwt).getHttpResponse();
	}

	/**
	 * Get all substitutes based on the substitute teacher.
	 *
	 * @see SubstituteController#getSubstitutesBySubstituteTeacher(String, long, DecodedJWT)
	 */
	@AuthScope("substitute.read.current")
	@GetMapping("/substitute/substituteTeacher")
	public Object getSubstitutesBySubstituteTeacher(@RequestParam @NotBlank String teacher,
													@RequestParam(required = false, defaultValue = "-1") long date,
													DecodedJWT jwt) {
		return this.substituteController.getSubstitutesBySubstituteTeacher(teacher, date, jwt).getHttpResponse();
	}

}
