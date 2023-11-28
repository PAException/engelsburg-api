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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	 * Create a {@link SubstituteModel} out of a {@link SubstituteDTO}.
	 *
	 * @param dto with information
	 * @return created substitute model
	 */
	private static SubstituteModel createSubstitute(SubstituteDTO dto) {
		return new SubstituteModel(
				-1,
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
		for (SubstituteDTO dto : fetchedDTOs) {
			if (current.stream().anyMatch(substituteDTO -> substituteDTO.sameBase(dto))) {
				if (!current.contains(dto)) updated.add(dto);
			} else created.add(dto);

			toSave.add(createSubstitute(dto));
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
		Set<String> classes = classNameFilter == null || classNameFilter.isBlank()
				? new HashSet<>()
				: new HashSet<>(Arrays.asList(classNameFilter.split(",")));
		Set<String> teachers = teacherFilter == null || teacherFilter.isBlank()
				? new HashSet<>()
				: new HashSet<>(Arrays.asList(teacherFilter.split(",")));
		final Date date = new Date(System.currentTimeMillis());

		final List<SubstituteModel> substitutes = new ArrayList<>();
		if (classes.isEmpty() && teachers.isEmpty()) {
			substitutes.addAll(this.substituteRepository.findAllByDateGreaterThanEqual(date));
		} else {
			for (SubstituteModel substitute : this.substituteRepository.findAllByDateGreaterThanEqual(date)) {
				List<String> classNames = substitute.getClassName() == null
						? new ArrayList<>()
						: SubstituteModel.splitClasses(substitute.getClassName());

				String teacher = substitute.getTeacher();
				String substituteTeacher = substitute.getSubstituteTeacher();

				if (classNames.isEmpty()) substitutes.add(substitute);
				else if (classes.stream().anyMatch(classNames::contains)) substitutes.add(substitute);
				else if (teacher != null && (teachers.contains(teacher) || teachers.contains(substituteTeacher))) {
					substitutes.add(substitute);
				}
			}
		}

		//If no substitutes available return error
		if (substitutes.isEmpty()) return Result.of(Error.NOT_FOUND, NAME_KEY);

		//Map substitutes to response dtos and return them
		List<SubstituteDTO> dtos = new ArrayList<>();
		for (SubstituteModel substitute : substitutes) dtos.add(substitute.toResponseDTO());
		return Result.of(new GetSubstitutesResponseDTO(dtos, timestamp));
	}
}
