/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.controller.reserved;

import io.github.paexception.engelsburg.api.controller.internal.UserController;
import io.github.paexception.engelsburg.api.database.model.SemesterModel;
import io.github.paexception.engelsburg.api.database.model.SubjectModel;
import io.github.paexception.engelsburg.api.database.repository.SemesterRepository;
import io.github.paexception.engelsburg.api.endpoint.dto.SemesterDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.UserDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.CreateSemesterRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.UpdateSemesterRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.GetSemestersResponseDTO;
import io.github.paexception.engelsburg.api.util.Error;
import io.github.paexception.engelsburg.api.util.Result;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import static io.github.paexception.engelsburg.api.util.Constants.Semester.NAME_KEY;

/**
 * Controller for semesters.
 */
@Component
public class SemesterController {


	private final SemesterRepository semesterRepository;
	private final UserController userController;
	private final SubjectController subjectController;
	private final TimetableController timetableController;
	private final GradeShareController gradeShareController;
	private int year;

	public SemesterController(SemesterRepository semesterRepository, UserController userController,
			TimetableController timetableController, SubjectController subjectController,
			GradeShareController gradeShareController) {
		this.semesterRepository = semesterRepository;
		this.userController = userController;
		this.timetableController = timetableController;
		this.subjectController = subjectController;
		this.gradeShareController = gradeShareController;
		this.updateSchoolYear();
	}

	/**
	 * Return className by semester.
	 *
	 * @param semester to return className
	 * @return className
	 */
	public static String classNameBySemester(int semester) {
		if (semester <= 11) return "5";
		if (semester <= 13) return "6";
		if (semester <= 15) return "7";
		if (semester <= 17) return "8";
		if (semester <= 19) return "9";
		if (semester <= 21) return "10";
		if (semester == 22) return "E1";
		if (semester == 23) return "E2";
		if (semester == 24) return "Q1";
		if (semester == 25) return "Q2";
		if (semester == 26) return "Q3";
		if (semester == 27) return "Q4";

		return "";
	}

	/**
	 * Update the school year on instantiating this class and on the 1st August.
	 */
	@Scheduled(cron = "0 0 0 1 8 *")
	public void updateSchoolYear() {
		this.year = Calendar.getInstance().get(Calendar.YEAR);
		if (Calendar.getInstance().get(Calendar.MONTH) < Calendar.AUGUST) this.year--;
	}

	/**
	 * Create new semester of user.
	 *
	 * @param dto     with information to create
	 * @param userDTO user information
	 * @return newly create semester
	 */
	public Result<SemesterDTO> createSemester(CreateSemesterRequestDTO dto, UserDTO userDTO) {
		//Get old semester, create new semester
		SemesterModel oldSemester = userDTO.user.getCurrentSemester();
		SemesterModel semester = this.semesterRepository.save(new SemesterModel(
				userDTO.user,
				dto.getSchoolYear() < 0 ? this.year : dto.getSchoolYear(),
				dto.getSemester(),
				dto.getClassSuffix()
		));

		//Check for flags in dto to copy content to new semester. Only copy if parent copy subjects is enabled and
		//if the old semester, the current semester of the user, is not null
		if (dto.isCopySubjects() && oldSemester != null) {
			//Copy subjects
			Map<SubjectModel, SubjectModel> subjects = this.subjectController.copySubjects(oldSemester, semester);

			//If enabled copy timetable and gradeShares
			if (dto.isCopyTimetable()) this.timetableController.copyTimetable(oldSemester, semester, subjects);
			if (dto.isCopyGradeShares()) this.gradeShareController.copyGradeShares(subjects);
		}

		//If enabled set newly created semester to current of user
		if (dto.isSetAsCurrentSemester()) this.userController.updateCurrentSemester(userDTO.user, semester);

		//Return result with newly created semester as dto
		return Result.of(semester.toResponseDTO());
	}

	/**
	 * Update an existing semester of user.
	 *
	 * @param dto     with updated information
	 * @param userDTO user information
	 * @return updated semester or error
	 */
	public Result<SemesterDTO> updateSemester(UpdateSemesterRequestDTO dto, UserDTO userDTO) {
		//Get semester by id, if not found return error
		Optional<SemesterModel> optionalSemester = this.semesterRepository.findById(dto.getSemesterId());
		if (optionalSemester.isEmpty()) return Result.of(Error.NOT_FOUND, NAME_KEY);

		//Check if user of semester is the requesting one or has superior permissions, if not return error
		SemesterModel semester = optionalSemester.get();
		if (!semester.getUser().is(userDTO) && !userDTO.hasScope("semester.write.all"))
			return Result.of(Error.FORBIDDEN, NAME_KEY);

		//Update fields if specified in dto
		if (dto.getSchoolYear() < 0) semester.setSchoolYear(dto.getSchoolYear());
		if (dto.getSemester() < 0) semester.setSemester(dto.getSemester());
		if (dto.getClassSuffix() != null) semester.setClassSuffix(dto.getClassSuffix());

		//Save and return updated semester
		return Result.of(this.semesterRepository.save(semester).toResponseDTO());
	}

	/**
	 * Get semester as model.
	 *
	 * @param semesterId identifier of semester
	 * @param userDTO    user information
	 * @return semester model
	 */
	public Result<SemesterModel> getSemesterRaw(int semesterId, UserDTO userDTO) {
		//Get semester by id, if not found return error
		Optional<SemesterModel> optionalSemester = this.semesterRepository.findById(semesterId);
		if (optionalSemester.isEmpty()) return Result.of(Error.NOT_FOUND, NAME_KEY);

		//Check if user of semester is the requesting one or has superior permissions, if not return error
		SemesterModel semester = optionalSemester.get();
		if (!semester.getUser().is(userDTO) && !userDTO.hasScope("semester.read.all"))
			return Result.of(Error.FORBIDDEN, NAME_KEY);

		//Return semester
		return Result.of(semester);
	}

	/**
	 * Get semester as dto.
	 *
	 * @param semesterId identifier of semester
	 * @param userDTO    user information
	 * @return semester response dto
	 */
	public Result<SemesterDTO> getSemester(int semesterId, UserDTO userDTO) {
		//Map semester to response dto and return result
		return this.getSemesterRaw(semesterId, userDTO).map(SemesterModel::toResponseDTO);
	}

	/**
	 * Get all semester of user.
	 *
	 * @param userDTO user of semesters
	 * @return all semesters as dto
	 */
	public Result<GetSemestersResponseDTO> getAllSemester(UserDTO userDTO) {
		//Get all semester of user, if empty return error
		List<SemesterModel> semester = this.semesterRepository.findAllByUser(userDTO.user);
		if (semester.isEmpty()) return Result.of(Error.NOT_FOUND, NAME_KEY);

		//Return result with all semesters mapped to dtos
		return Result.of(new GetSemestersResponseDTO(
				semester.stream().map(SemesterModel::toResponseDTO).collect(Collectors.toList())
		));
	}

	/**
	 * Delete a semester by id.
	 *
	 * @param semesterId identifier of semester
	 * @param userDTO    user information
	 * @return empty result or error
	 */
	public Result<?> deleteSemester(int semesterId, UserDTO userDTO) {
		//Get optional semester, if not found return error
		Optional<SemesterModel> optionalSemester = this.semesterRepository.findById(semesterId);
		if (optionalSemester.isEmpty()) return Result.of(Error.NOT_FOUND, NAME_KEY);

		//Check if user of semester is the requesting one or has superior permissions, if not return error
		SemesterModel semester = optionalSemester.get();
		if (!semester.getUser().is(userDTO) && !userDTO.hasScope("semester.delete.all"))
			return Result.of(Error.FORBIDDEN, NAME_KEY);

		//Fail if subjects, timetable or tasks are referencing to this subject
		if (semester.hasDepending()) return Result.of(Error.FAILED_DEPENDENCY, NAME_KEY);

		//Delete semester and return empty result
		this.semesterRepository.delete(semester);
		return Result.empty();
	}
}
