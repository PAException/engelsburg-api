/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.controller.reserved;

import io.github.paexception.engelsburg.api.database.model.GradeShareModel;
import io.github.paexception.engelsburg.api.database.model.SemesterModel;
import io.github.paexception.engelsburg.api.database.model.SubjectModel;
import io.github.paexception.engelsburg.api.database.repository.GradeShareRepository;
import io.github.paexception.engelsburg.api.endpoint.dto.GradeShareDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.UserDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.CreateGradeShareRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.CreateSemesterRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.UpdateGradeShareRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.GetGradeSharesDTO;
import io.github.paexception.engelsburg.api.util.Error;
import io.github.paexception.engelsburg.api.util.Result;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static io.github.paexception.engelsburg.api.util.Constants.GradeShare.NAME_KEY;

/**
 * Controller for GradesShares.
 */
@Component
@AllArgsConstructor
public class GradeShareController {

	private final GradeShareRepository gradeShareRepository;
	private final SubjectController subjectController;

	/**
	 * Create new gradeShare.
	 *
	 * @param dto     with information of gradeShare to create
	 * @param userDTO user of gradeShare
	 * @return created gradeShare or error if subject wasn't found
	 */
	public Result<GradeShareDTO> create(CreateGradeShareRequestDTO dto, UserDTO userDTO) {
		//Get subject by subjectId, on error return
		Result<SubjectModel> result = this.subjectController.getSubjectRaw(dto.getSubjectId(), userDTO);
		if (result.isErrorPresent()) return Result.ret(result);

		//Create a new gradeShare and return dto
		return Result.of(this.gradeShareRepository.save(new GradeShareModel(
				-1,
				result.getResult(),
				dto.getShare(),
				dto.getName()
		)).toResponseDTO());
	}

	/**
	 * Update an existing gradeShare.
	 *
	 * @param dto     with partial optional update information
	 * @param userDTO User of gradeShare
	 * @return updated gradeShare or error if gradeShare wasn't found or lacking permissions
	 */
	public Result<GradeShareDTO> update(UpdateGradeShareRequestDTO dto, UserDTO userDTO) {
		//Get gradeShare by gradeShareId, if not found return error
		Optional<GradeShareModel> optionalGradeShare = this.gradeShareRepository.findById(dto.getGradeShareId());
		if (optionalGradeShare.isEmpty()) return Result.of(Error.NOT_FOUND, NAME_KEY);

		//Check if user of gradeShare is the requesting one or has superior permissions, if not return error
		GradeShareModel gradeShare = optionalGradeShare.get();
		if (!gradeShare.getSubject().getSemester().getUser().is(userDTO) && !userDTO.hasScope("grade.share.write.all"))
			return Result.of(Error.FORBIDDEN, NAME_KEY);

		//Update specified values
		if (dto.getShare() >= 0 && dto.getShare() <= 1) gradeShare.setShare(dto.getShare());
		if (dto.getName() != null && !dto.getName().isBlank()) gradeShare.setName(dto.getName());

		//Return updated gradeShare
		return Result.of(this.gradeShareRepository.save(gradeShare).toResponseDTO());
	}

	/**
	 * Get a {@link GradeShareModel} by gradeShareId and user.
	 *
	 * @param gradeShareId to get gradeShare
	 * @param userDTO      User of gradeShare
	 * @return Result with {@link GradeShareModel} or error
	 */
	public Result<GradeShareModel> getRaw(int gradeShareId, UserDTO userDTO) {
		//Get gradeShare by gradeShareId, if not found return error
		Optional<GradeShareModel> optionalGradeShare = this.gradeShareRepository.findById(gradeShareId);
		if (optionalGradeShare.isEmpty()) return Result.of(Error.NOT_FOUND, NAME_KEY);

		//Check if user of gradeShare is the requesting one or has superior permissions, if not return error
		GradeShareModel gradeShare = optionalGradeShare.get();
		if (!gradeShare.getSubject().getSemester().getUser().is(userDTO) && !userDTO.hasScope("grade.share.read.all"))
			return Result.of(Error.FORBIDDEN, NAME_KEY);

		//Return gradeShare
		return Result.of(optionalGradeShare.get());
	}

	/**
	 * Map {@link GradeShareModel} to response dto.
	 *
	 * @param gradeShareId to
	 * @param userDTO      User of gradeShare
	 * @return Result of dto or error
	 * @see #getRaw(int, UserDTO)
	 */
	public Result<GradeShareDTO> get(int gradeShareId, UserDTO userDTO) {
		//Map gradeShare to dto
		return this.getRaw(gradeShareId, userDTO).map(GradeShareModel::toResponseDTO);
	}

	/**
	 * Get a List of {@link GradeShareModel}s by subject or user.
	 *
	 * @param subjectId (optional) to filter by subject
	 * @param userDTO   user requesting
	 * @param semester  semester of gradeShares
	 * @return Result with List of {@link GradeShareModel}s or error
	 */
	public Result<List<GradeShareModel>> getAllRaw(int subjectId, UserDTO userDTO, SemesterModel semester) {
		List<SubjectModel> subjects = new ArrayList<>();
		if (subjectId > 0) { //If subject is given
			//Find subject, if error return, otherwise add to subjects
			Result<SubjectModel> result = this.subjectController.getSubjectRaw(subjectId, userDTO);
			if (result.isErrorPresent()) return Result.ret(result);
			else subjects.add(result.getResult());
		} else { //If subject is not given
			//Find all subjects, if error return, otherwise add to subjects
			Result<List<SubjectModel>> result = this.subjectController.getAllSubjectsRaw(semester);
			if (result.isErrorPresent()) return Result.ret(result);
			else subjects.addAll(result.getResult());
		}

		//Get all gradeShares of added subjects, if none found return error
		List<GradeShareModel> gradeShares = new ArrayList<>();
		for (SubjectModel subject : subjects)
			gradeShares.addAll(this.gradeShareRepository.findAllBySubject(subject));
		if (gradeShares.isEmpty()) return Result.of(Error.NOT_FOUND, NAME_KEY);

		//Return all gradeShares
		return Result.of(gradeShares);
	}

	/**
	 * Get all gradeShares of a user filtered by optional subject.
	 *
	 * @param subjectId to get optional subject
	 * @param userDTO   User of gradeShares
	 * @param semester  semester of user
	 * @return Result with gradeShares or error
	 * @see #getAllRaw(int, UserDTO, SemesterModel)
	 */
	public Result<GetGradeSharesDTO> getAll(int subjectId, UserDTO userDTO, SemesterModel semester) {
		//Map all gradeShares to dtos
		return this.getAllRaw(subjectId, userDTO, semester).map(gradeShareModels -> new GetGradeSharesDTO(
				gradeShareModels.stream().map(GradeShareModel::toResponseDTO).collect(Collectors.toList())));
	}

	/**
	 * Delete a gradeShares by its id.
	 *
	 * @param gradeShareId ID of gradeShare to delete
	 * @param userDTO      User of gradeShare
	 * @return Empty result or error
	 */
	public Result<?> delete(int gradeShareId, UserDTO userDTO) {
		//Get gradeShare by gradeShareId, if not found return error
		Optional<GradeShareModel> optionalGradeShare = this.gradeShareRepository.findById(gradeShareId);
		if (optionalGradeShare.isEmpty()) return Result.of(Error.NOT_FOUND, NAME_KEY);

		//Check if user of gradeShare is the requesting one or has superior permissions, if not return error
		GradeShareModel gradeShare = optionalGradeShare.get();
		if (!gradeShare.getSubject().getSemester().getUser().is(userDTO) && !userDTO.hasScope("grade.share.delete.all"))
			return Result.of(Error.FORBIDDEN, NAME_KEY);

		//Fail if grades referencing to this gradeShare
		if (gradeShare.hasDepending()) return Result.of(Error.FAILED_DEPENDENCY, NAME_KEY);

		//Delete gradeShare and return empty result
		this.gradeShareRepository.delete(gradeShare);
		return Result.empty();
	}

	/**
	 * Copy gradeShares from subjects to subject.
	 * Usually called by {@link SemesterController#createSemester(CreateSemesterRequestDTO, UserDTO)}
	 *
	 * @param subjects a map of subjects with old as key and new as value to replace subjects in timetable entry
	 */
	public void copyGradeShares(Map<SubjectModel, SubjectModel> subjects) {
		//Get all gradeShares from old subjects
		Stream<GradeShareModel> gradeShares = subjects.keySet().stream()
				.flatMap(subject -> this.gradeShareRepository.findAllBySubject(subject).stream());
		for (GradeShareModel gradeShare : gradeShares.collect(Collectors.toList())) {
			//Change to new gradeShare, empty grades and update subject to new
			gradeShare.setGradeShareId(-1);
			gradeShare.setGrades(List.of());
			gradeShare.setSubject(subjects.get(gradeShare.getSubject()));

			//Copy gradeShare
			this.gradeShareRepository.save(gradeShare);
		}
	}
}
