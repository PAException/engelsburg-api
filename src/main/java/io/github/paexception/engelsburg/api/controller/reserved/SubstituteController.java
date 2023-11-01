/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.controller.reserved;

import io.github.paexception.engelsburg.api.database.model.SubstituteModel;
import io.github.paexception.engelsburg.api.database.repository.SubstituteRepository;
import io.github.paexception.engelsburg.api.endpoint.dto.SubstituteDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.GetSubstitutesResponseDTO;
import io.github.paexception.engelsburg.api.service.notification.NotificationService;
import io.github.paexception.engelsburg.api.service.scheduled.SubstituteUpdateService;
import io.github.paexception.engelsburg.api.util.Error;
import io.github.paexception.engelsburg.api.util.Result;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.github.paexception.engelsburg.api.util.Constants.Substitute.NAME_KEY;

/**
 * Controller for substitutes.
 */
@Component
@AllArgsConstructor
public class SubstituteController {

	private static long timestamp = 0;

	private final SubstituteRepository substituteRepository;
	private final NotificationService notificationService;


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
		timestamp = System.currentTimeMillis();

		List<SubstituteDTO> current = new ArrayList<>();
		for (SubstituteModel substitute : this.substituteRepository.findAllByDate(date)) {
			current.add(substitute.toResponseDTO());
		}
		this.substituteRepository.deleteAllByDate(date);

		//Check if substitutes have been updated or newly created
		List<SubstituteDTO> updated = new ArrayList<>(), created = new ArrayList<>();
		List<SubstituteModel> toSave = new ArrayList<>();
		for (SubstituteDTO dto: fetchedDTOs) {
			if (current.contains(dto)) updated.add(dto);
			else created.add(dto);

			toSave.add(createSubstitute(-1, dto));
		}

		this.substituteRepository.saveAll(toSave);

		//Send notifications if lists are not empty
		if (!updated.isEmpty()) this.notificationService.sendSubstituteNotifications(updated, false);
		if (!created.isEmpty()) this.notificationService.sendSubstituteNotifications(created, true);
	}

	/**
	 * Get substitutes by specific filter.
	 * All parameters are optional.
	 *
	 * @param classNameFilter (optional) filter by className
	 * @param teacherFilter   (optional) filter by teacher
	 * @return substitutes with specific filters
	 */
	public Result<GetSubstitutesResponseDTO> getSubstitutes(String classNameFilter, String teacherFilter) {
		List<String> classes = classNameFilter == null || classNameFilter.isBlank()
				? new ArrayList<>()
				: Arrays.asList(classNameFilter.split(","));
		List<String> teacher = teacherFilter == null || teacherFilter.isBlank()
				? new ArrayList<>()
				: Arrays.asList(teacherFilter.split(","));
		final Date date = new Date(System.currentTimeMillis());

		//Get all substitute based on optional parameters
		List<SubstituteModel> substitutes = new ArrayList<>();
		if (teacher.isEmpty() && classes.isEmpty()) {
			substitutes = this.substituteRepository.findAllByDateGreaterThanEqual(date);
		}
		if (!classes.isEmpty()) {
			substitutes.addAll(this.substituteRepository.findAllByDateGreaterThanEqualAndClassNameIn(date, classes));
		}
		if (!teacher.isEmpty()) {
			substitutes.addAll(
					this.substituteRepository.findAllByDateGreaterThanEqualAndTeacherInOrDateGreaterThanEqualAndSubstituteTeacherIn(
							date, teacher, date, teacher
					)
			);
		}

		//If no substitutes available return error
		if (substitutes.isEmpty()) return Result.of(Error.NOT_FOUND, NAME_KEY);

		//Map substitutes to response dtos and return them
		List<SubstituteDTO> dtos = new ArrayList<>();
		for (SubstituteModel substitute : substitutes) dtos.add(substitute.toResponseDTO());
		return Result.of(new GetSubstitutesResponseDTO(dtos, timestamp));
	}
}
