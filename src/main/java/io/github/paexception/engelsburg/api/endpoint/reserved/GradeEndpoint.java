package io.github.paexception.engelsburg.api.endpoint.reserved;

import io.github.paexception.engelsburg.api.controller.reserved.GradeController;
import io.github.paexception.engelsburg.api.endpoint.dto.UserDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.CreateGradeRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.UpdateGradeRequestDTO;
import io.github.paexception.engelsburg.api.spring.auth.AuthScope;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;

/**
 * RestController for grade actions.
 */
@RestController
public class GradeEndpoint {

	private final GradeController gradeController;

	public GradeEndpoint(GradeController gradeController) {
		this.gradeController = gradeController;
	}

	/**
	 * Create a new grade.
	 *
	 * @see GradeController#createGrade(CreateGradeRequestDTO, UserDTO)
	 */
	@AuthScope("grade.write.self")
	@PostMapping("/grade")
	public Object createGrade(@RequestBody @Valid CreateGradeRequestDTO dto, UserDTO userDTO) {
		return this.gradeController.createGrade(dto, userDTO).getHttpResponse();
	}

	/**
	 * Update a grade.
	 *
	 * @see GradeController#updateGrade(UpdateGradeRequestDTO, UserDTO)
	 */
	@AuthScope("grade.write.self")
	@PatchMapping("/grade")
	public Object updateGrade(@RequestBody @Valid UpdateGradeRequestDTO dto, UserDTO userDTO) {
		return this.gradeController.updateGrade(dto, userDTO).getHttpResponse();
	}

	/**
	 * Get all grades or by subject.
	 *
	 * @see GradeController#getGrades(String, UserDTO)
	 */
	@AuthScope("grade.read.self")
	@GetMapping("/grade")
	public Object getGrade(@RequestParam(value = "subject", required = false) String subject, UserDTO userDTO) {
		return this.gradeController.getGrades(subject, userDTO).getHttpResponse();
	}

	/**
	 * Delete a grade by gradeId.
	 *
	 * @see GradeController#deleteGrade(int, UserDTO)
	 */
	@AuthScope("grade.delete.self")
	@DeleteMapping("/grade/{gradeId}")
	public Object deleteGrade(@PathVariable int gradeId, UserDTO userDTO) {
		return this.gradeController.deleteGrade(gradeId, userDTO).getHttpResponse();
	}

}
