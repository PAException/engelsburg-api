/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.controller.reserved;

import io.github.paexception.engelsburg.api.database.model.GradeModel;
import io.github.paexception.engelsburg.api.database.model.GradeShareModel;
import io.github.paexception.engelsburg.api.database.model.SemesterModel;
import io.github.paexception.engelsburg.api.database.repository.GradeRepository;
import io.github.paexception.engelsburg.api.endpoint.dto.GradeDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.UserDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.CreateGradeRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.UpdateGradeRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.GetGradesResponseDTO;
import io.github.paexception.engelsburg.api.util.Error;
import io.github.paexception.engelsburg.api.util.Result;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import static io.github.paexception.engelsburg.api.util.Constants.Grade.NAME_KEY;

/**
 * Controller for grades.
 */
@Component
@AllArgsConstructor
public class GradeController {

	private final GradeRepository gradeRepository;
	private final GradeShareController gradeShareController;

	/**
	 * Create new grade.
	 *
	 * @param dto     with grade information
	 * @param userDTO with userId
	 * @return created grade
	 */
	public Result<GradeDTO> createGrade(CreateGradeRequestDTO dto, UserDTO userDTO) {
		//Get gradeShare by id, if error return
		Result<GradeShareModel> result = this.gradeShareController.getRaw(dto.getGradeShareId(), userDTO);
		if (result.isErrorPresent()) return Result.ret(result);

		//Create and return newly created grade
		return Result.of(this.gradeRepository.save(new GradeModel(
				-1,
				result.getResult(), //GradeShare
				dto.getName(),
				dto.getValue()
		)).toResponseDTO());
	}

	/**
	 * Update an existing grade.
	 *
	 * @param dto     with gradeId and information to change
	 * @param userDTO with userId
	 * @return updated grade
	 */
	public Result<GradeDTO> updateGrade(UpdateGradeRequestDTO dto, UserDTO userDTO) {
		//Get grade by id, if not present return error
		Optional<GradeModel> optionalGrade = this.gradeRepository.findById(dto.getGradeId());
		if (optionalGrade.isEmpty()) return Result.of(Error.NOT_FOUND, NAME_KEY);

		//Check if user of grade is the requesting one or has superior permissions, if not return error
		GradeModel grade = optionalGrade.get();
		if (!grade.getGradeShare().getSubject().getSemester().getUser().is(userDTO)
				&& !userDTO.hasScope("grade.write.all"))
			return Result.of(Error.FORBIDDEN, NAME_KEY);

		//Update values of grade if they were specified in dto
		if (dto.getName() != null && !dto.getName().isBlank()) grade.setName(dto.getName());
		if (dto.getValue() >= 0) grade.setValue(dto.getValue());
		if (dto.getGradeShareId() >= 0) {
			//Get gradeShare by id, if error return
			Result<GradeShareModel> result = this.gradeShareController.getRaw(dto.getGradeShareId(), userDTO);
			if (result.isErrorPresent()) return Result.ret(result);

			grade.setGradeShare(result.getResult());
		}

		//Return updated grade
		return Result.of(this.gradeRepository.save(grade).toResponseDTO());
	}

	/**
	 * Get all grades of user or filter by subject.
	 *
	 * @param subjectId (optional)
	 * @param userDTO   with userId
	 * @param semester  semester of user
	 * @return list of grades
	 */
	@Transactional
	public Result<GetGradesResponseDTO> getGrades(int subjectId, UserDTO userDTO, SemesterModel semester) {
		//Get all grades, optional filter by subject. If error occurred return
		Result<List<GradeShareModel>> result = this.gradeShareController.getAllRaw(subjectId, userDTO, semester);
		if (result.isErrorPresent()) return Result.ret(result);

		//Map all gradeShares to gradeDTOs by getting all grades of a gradeShare and mapping to dtos
		List<GradeDTO> dtos = result.getResult().stream()
				.flatMap(gradeShare -> this.gradeRepository.findAllByGradeShare(gradeShare).stream())
				.map(GradeModel::toResponseDTO)
				.collect(Collectors.toList());
		//If empty return error
		if (dtos.isEmpty()) return Result.of(Error.NOT_FOUND, NAME_KEY);

		//Return grades
		return Result.of(new GetGradesResponseDTO(dtos));
	}

	/**
	 * Delete grade.
	 *
	 * @param gradeId to delete
	 * @param userDTO with userId
	 * @return empty result
	 */
	@Transactional
	public Result<?> deleteGrade(int gradeId, UserDTO userDTO) {
		//Get grade by id, if not present return error
		Optional<GradeModel> optionalGrade = this.gradeRepository.findById(gradeId);
		if (optionalGrade.isEmpty()) return Result.of(Error.NOT_FOUND, NAME_KEY);

		//Check if user of grade is the requesting one or has superior permissions, if not return error
		GradeModel grade = optionalGrade.get();
		if (!grade.getGradeShare().getSubject().getSemester().getUser().is(userDTO)
				&& !userDTO.hasScope("grade.delete.all"))
			return Result.of(Error.FORBIDDEN, NAME_KEY);

		//Delete grade and return empty result
		this.gradeRepository.delete(grade);
		return Result.empty();
	}
}
