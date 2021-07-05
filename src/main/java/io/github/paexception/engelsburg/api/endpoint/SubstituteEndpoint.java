package io.github.paexception.engelsburg.api.endpoint;

import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.paexception.engelsburg.api.controller.SubstituteController;
import io.github.paexception.engelsburg.api.endpoint.dto.request.GetSubstitutesByClassNameRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.GetSubstitutesBySubstituteTeacherRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.GetSubstitutesByTeacherRequestDTO;
import io.github.paexception.engelsburg.api.spring.auth.AuthScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;

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
	 * @param dto information and filters to get substitutes
	 * @return found substitutes
	 */
	@AuthScope("substitute.read.current")
	@GetMapping("/substitute/className")
	public Object getSubstitutesByClassName(@RequestBody @Valid GetSubstitutesByClassNameRequestDTO dto, DecodedJWT jwt) {
		return this.substituteController.getSubstitutesByClassName(dto, jwt).getHttpResponse();
	}

	/**
	 * Get all substitutes based on the teacher.
	 *
	 * @param dto filter for substitutes
	 * @return found substitutes
	 */
	@AuthScope("substitute.read.current")
	@GetMapping("/substitute/teacher")
	public Object getSubstitutesByTeacher(@RequestBody @Valid GetSubstitutesByTeacherRequestDTO dto, DecodedJWT jwt) {
		return this.substituteController.getSubstitutesByTeacher(dto, jwt).getHttpResponse();
	}

	/**
	 * Get all substitutes based on the substitute teacher.
	 *
	 * @param dto filter for substitutes
	 * @return found substitutes
	 */
	@AuthScope("substitute.read.current")
	@GetMapping("/substitute/substituteTeacher")
	public Object getSubstitutesBySubstituteTeacher(@RequestBody @Valid GetSubstitutesBySubstituteTeacherRequestDTO dto, DecodedJWT jwt) {
		return this.substituteController.getSubstitutesBySubstituteTeacher(dto, jwt).getHttpResponse();
	}

}
