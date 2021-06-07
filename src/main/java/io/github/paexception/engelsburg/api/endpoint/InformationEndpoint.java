package io.github.paexception.engelsburg.api.endpoint;

import io.github.paexception.engelsburg.api.controller.InformationController;
import io.github.paexception.engelsburg.api.spring.AuthScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * RestController for information actions
 */
@RestController
public class InformationEndpoint {

	@Autowired
	private InformationController informationController;

	/**
	 * Get information about a specific teacher
	 *
	 * @see InformationController#getTeacher(String)
	 */
	@AuthScope("info.teacher.read.all")
	@GetMapping("/info/teacher/{abbreviation}")
	public Object getTeacher(@PathVariable("abbreviation") String abbreviation) {
		return this.informationController.getTeacher(abbreviation).getHttpResponse();
	}

	/**
	 * Get all current classes
	 *
	 * @see InformationController#getCurrentClasses()
	 */
	@AuthScope("info.classes.read.all")
	@GetMapping("/info/classes")
	public Object getCurrentClasses() {
		return this.informationController.getCurrentClasses().getHttpResponse();
	}

	/**
	 * Get all known teachers
	 *
	 * @see InformationController#getAllTeachers()
	 */
	@AuthScope("info.teacher.read.all")
	@GetMapping("/info/teacher")
	public Object getTeachers() {
		return this.informationController.getAllTeachers().getHttpResponse();
	}

}
