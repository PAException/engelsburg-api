/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.controller.reserved;

import io.github.paexception.engelsburg.api.database.model.SubstituteModel;
import io.github.paexception.engelsburg.api.database.repository.SubstituteRepository;
import io.github.paexception.engelsburg.api.endpoint.dto.SubstituteDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.UserDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.GetSubstitutesResponseDTO;
import io.github.paexception.engelsburg.api.service.notification.NotificationService;
import io.github.paexception.engelsburg.api.service.scheduled.SubstituteUpdateService;
import io.github.paexception.engelsburg.api.util.Error;
import io.github.paexception.engelsburg.api.util.Result;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Component;
import javax.transaction.Transactional;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static io.github.paexception.engelsburg.api.util.Constants.Substitute.NAME_KEY;

/**
 * Controller for substitutes.
 */
@Component
@AllArgsConstructor
public class SubstituteController {

	private final SubstituteRepository substituteRepository;
	private final NotificationService notificationService;

	/**
	 * Checks if sender has permission to get past substitutes.
	 *
	 * @param userDTO user information
	 * @param date    specified
	 * @return true if permitted, false if not
	 */
	private static boolean pastTimeCheck(UserDTO userDTO, long date) {
		//Return true if user has superior scope, date is greater than current millis or date is the current day
		return userDTO.hasScope("substitute.read.all")
				|| System.currentTimeMillis() <= date
				|| DateUtils.isSameDay(new Date(System.currentTimeMillis()), new Date(date));
	}

	/**
	 * Checks if a string is not blank, empty or null.
	 *
	 * @param value string to check
	 * @return true if given string is not blank or null
	 */
	private static boolean notBlank(String value) {
		return value != null && !value.isBlank();
	}

	/**
	 * Create a {@link SubstituteModel} out of a {@link SubstituteDTO}.
	 *
	 * @param substituteId id of substitute
	 * @param dto          with information
	 * @return created substitute model
	 */
	private static SubstituteModel createSubstitute(int substituteId, SubstituteDTO dto) {
		return new SubstituteModel(
				substituteId,
				dto.getDate(),
				dto.getClassName(),
				dto.getLesson(),
				dto.getSubject(),
				dto.getSubstituteTeacher() != null ? dto.getSubstituteTeacher().toUpperCase() : null,
				dto.getTeacher() != null ? dto.getTeacher().toUpperCase() : null,
				dto.getType(),
				dto.getSubstituteOf(),
				dto.getRoom(),
				dto.getText()
		);
	}

	/**
	 * Update substitutes.
	 * Only {@link SubstituteUpdateService} is supposed to call
	 * this function!
	 *
	 * @param fetchedDTOs with all crawled substitutes
	 * @param date        of substitutes
	 */
	@Transactional
	public void updateSubstitutes(List<SubstituteDTO> fetchedDTOs, Date date) {
		//Get all substitutes by date, remove all which are also in fetched dtos
		for (SubstituteModel substitute : this.substituteRepository.findAllByDate(date))
			fetchedDTOs.remove(substitute.toResponseDTO());

		//Check if substitutes have been updated or newly created
		List<SubstituteDTO> updated = new ArrayList<>(), created = new ArrayList<>();
		for (SubstituteDTO dto : fetchedDTOs) {
			//Get optional substitute based on dto information
			Optional<SubstituteModel> optionalSubstitute;
			if (Character.isDigit(dto.getClassName().charAt(0))) { //5a-10e
				optionalSubstitute = this.substituteRepository
						.findByDateAndLessonAndClassNameLike(date, dto.getLesson(), dto.getClassName());
			} else if (notBlank(dto.getTeacher())) { //E1-Q4 with teacher
				optionalSubstitute = this.substituteRepository
						.findByDateAndLessonAndTeacher(date, dto.getLesson(), dto.getTeacher());
			} else { //E1-Q4 without teacher
				optionalSubstitute = this.substituteRepository
						.findByDateAndLessonAndSubject(date, dto.getLesson(), dto.getSubject());
			}

			//Check if the substitute was newly created or updated
			optionalSubstitute.ifPresentOrElse(
					//Update substitute and add to updated list
					substitute -> {
						this.substituteRepository.save(createSubstitute(substitute.getSubstituteId(), dto));
						updated.add(dto);
					},
					//Save newly created substitute and add to created list
					() -> {
						this.substituteRepository.save(createSubstitute(-1, dto));
						created.add(dto);
					}
			);
		}

		//Send notifications if lists are not empty
		if (!updated.isEmpty()) this.notificationService.sendSubstituteNotifications(updated, false);
		if (!created.isEmpty()) this.notificationService.sendSubstituteNotifications(created, true);
	}

	/**
	 * Get substitutes by specific filter.
	 * All parameters are optional. If date is not provided it will be set to the current day.
	 *
	 * @param date              (optional) filter by date, if not provided will be set to current date
	 * @param lesson            (optional) filter by lesson
	 * @param className         (optional) filter by className
	 * @param substituteTeacher (optional) filter by substituteTeacher
	 * @param teacher           (optional) filter by teacher
	 * @param userDTO           to check permission to access substitutes in the past
	 * @return substitutes with specific filters
	 */
	public Result<GetSubstitutesResponseDTO> getSubstitutes(
			long date,
			int lesson,
			String className,
			String substituteTeacher,
			String teacher,
			UserDTO userDTO
	) {
		//If date not specified set to current day
		if (date < 0) date = System.currentTimeMillis();
		//Check if date is in the past and if so user has permission to access substitutes in the past
		if (!pastTimeCheck(userDTO, date)) return Result.of(Error.FORBIDDEN, NAME_KEY);
		Date sqlDate = new Date(date);

		//Get all substitute based on optional parameters
		List<SubstituteModel> substitutes;
		if (lesson >= 0 && notBlank(className) && notBlank(substituteTeacher) && notBlank(teacher)) {
			substitutes = this.substituteRepository.findAllByDateAndLessonAndClassNameAndSubstituteTeacherAndTeacher(
					sqlDate,
					lesson,
					className,
					substituteTeacher,
					teacher
			);
		} else if (lesson >= 0 && notBlank(className) && notBlank(substituteTeacher)) {
			substitutes = this.substituteRepository.findAllByDateAndLessonAndClassNameAndSubstituteTeacher(
					sqlDate,
					lesson,
					className,
					substituteTeacher
			);
		} else if (lesson >= 0 && notBlank(className)) {
			substitutes = this.substituteRepository.findAllByDateAndLessonAndClassName(
					sqlDate,
					lesson,
					className
			);
		} else if (lesson >= 0) {
			substitutes = this.substituteRepository.findAllByDateAndLesson(
					sqlDate,
					lesson
			);
		} else if (notBlank(className) && notBlank(substituteTeacher) && notBlank(teacher)) {
			substitutes = this.substituteRepository.findAllByDateAndClassNameAndSubstituteTeacherAndTeacher(
					sqlDate,
					className,
					substituteTeacher,
					teacher
			);
		} else if (notBlank(className) && notBlank(substituteTeacher)) {
			substitutes = this.substituteRepository.findAllByDateAndClassNameAndSubstituteTeacher(
					sqlDate,
					className,
					substituteTeacher
			);
		} else if (notBlank(className)) {
			substitutes = this.substituteRepository.findAllByDateAndClassName(
					sqlDate,
					className
			);
		} else if (notBlank(substituteTeacher) && notBlank(teacher)) {
			substitutes = this.substituteRepository.findAllByDateAndSubstituteTeacherAndTeacher(
					sqlDate,
					substituteTeacher,
					teacher
			);
		} else if (notBlank(substituteTeacher)) {
			substitutes = this.substituteRepository.findAllByDateAndSubstituteTeacher(
					sqlDate,
					substituteTeacher
			);
		} else if (notBlank(teacher)) {
			substitutes = this.substituteRepository.findAllByDateAndTeacher(
					sqlDate,
					teacher
			);
		} else substitutes = this.substituteRepository.findAllByDate(sqlDate);

		//If no substitutes available return error
		if (substitutes.isEmpty()) return Result.of(Error.NOT_FOUND, NAME_KEY);

		//Map substitutes to response dtos and return them
		List<SubstituteDTO> dtos = new ArrayList<>();
		for (SubstituteModel substitute : substitutes) dtos.add(substitute.toResponseDTO());
		return Result.of(new GetSubstitutesResponseDTO(dtos));
	}
}
