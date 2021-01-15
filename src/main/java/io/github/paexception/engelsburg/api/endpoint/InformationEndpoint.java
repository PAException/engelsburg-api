package io.github.paexception.engelsburg.api.endpoint;

import io.github.paexception.engelsburg.api.controller.InformationController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InformationEndpoint {

	@Autowired private InformationController informationController;

	@GetMapping("/info/classes")
	public Object getCurrentClasses() {
		return this.informationController.getCurrentClasses().getHttpResponse();
	}

	@GetMapping("/info/teacher")
	public Object getTeachers() {
		return this.informationController.getAllTeachers().getHttpResponse();
	}

	@GetMapping("/info/teacher/{abbreviation}")
	public Object getTeacher(@PathVariable("abbreviation") String abbreviation) {
		return this.informationController.getTeacher(abbreviation).getHttpResponse();
	}

	@GetMapping("/info/job/{job}")
	public Object resolveJobs(@PathVariable("job") long job) {
		return this.informationController.resolveJobs(job).getHttpResponse();
	}

}
