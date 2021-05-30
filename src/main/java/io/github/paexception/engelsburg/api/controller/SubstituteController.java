package io.github.paexception.engelsburg.api.controller;

import io.github.paexception.engelsburg.api.database.model.SubstituteModel;
import io.github.paexception.engelsburg.api.database.repository.SubstituteRepository;
import io.github.paexception.engelsburg.api.endpoint.dto.SubstituteDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.GetSubstitutesByClassNameRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.GetSubstitutesBySubstituteTeacherRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.GetSubstitutesByTeacherRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.GetSubstitutesResponseDTO;
import io.github.paexception.engelsburg.api.service.NotificationService;
import io.github.paexception.engelsburg.api.util.Error;
import io.github.paexception.engelsburg.api.util.Result;
import io.github.paexception.engelsburg.api.util.Validation;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;
import static io.github.paexception.engelsburg.api.util.Constants.Substitute.NAME_KEY;

/**
 * Controller for substitutes
 */
@Component
public class SubstituteController {

	@Autowired
	private SubstituteRepository substituteRepository;
	@Autowired
	private NotificationService notificationService;

	/**
	 * Update substitutes
	 * Only {@link io.github.paexception.engelsburg.api.service.SubstituteUpdateService} is supposed to call
	 * this function!
	 *
	 * @param fetchedDTOs with all crawled substitutes
	 */
	public void updateSubstitutes(List<SubstituteDTO> fetchedDTOs, Date date) {
		this.substituteRepository.findAllByDate(date).stream().map(SubstituteModel::toResponseDTO)
				.forEach(dto -> fetchedDTOs.removeIf(fetchedDTO -> fetchedDTO.equals(dto)));//Filter new or changed substitutes
		this.notificationService.sendSubstituteNotifications(fetchedDTOs);//Send notifications

		fetchedDTOs.forEach(dto -> {
			if (Character.isDigit(dto.getClassName().charAt(0))) {//5a-10e
				this.substituteRepository.deleteAllByDateAndLessonAndClassNameMatchesRegex(
						date, dto.getLesson(), dto.getClassName());
			} else {//E1-Q4
				if (dto.getTeacher().isBlank()) this.substituteRepository.deleteAllByDateAndLessonAndSubject(
						date, dto.getLesson(), dto.getSubject());
				else
					this.substituteRepository.deleteAllByDateAndLessonAndTeacher(date, dto.getLesson(), dto.getTeacher());
			}
			this.substituteRepository.save(this.createSubstitute(dto));
		});//Save new
	}

	/**
	 * Get all substitutes by Teacher
	 *
	 * @param dto with information
	 * @return all found substitutes
	 */
	public Result<GetSubstitutesResponseDTO> getSubstitutesByTeacher(GetSubstitutesByTeacherRequestDTO dto) {
		if (!(DateUtils.isSameDay(new Date(System.currentTimeMillis()), new Date(dto.getDate()))
				|| System.currentTimeMillis() < dto.getDate()) && dto.getDate() != 0)
			return Result.of(Error.INVALID_PARAM, "Date can't be in the past");

		List<SubstituteModel> substitutes;
		if (Validation.validateNotNullOrEmpty(dto.getLesson())) {
			if (Validation.validateNotNullOrEmpty(dto.getClassName())) {
				if (dto.getDate() == 0) {
					substitutes = this.substituteRepository.findAllByDateGreaterThanEqualAndTeacherAndLessonContainingAndClassName(
							new Date(System.currentTimeMillis()), dto.getTeacher(), dto.getLesson(), dto.getClassName()
					);
				} else substitutes = this.substituteRepository.findAllByDateAndTeacherAndLessonContainingAndClassName(
						new Date(dto.getDate()), dto.getTeacher(), dto.getLesson(), dto.getClassName());
			} else {
				if (dto.getDate() == 0) {
					substitutes = this.substituteRepository.findAllByDateGreaterThanEqualAndTeacherAndLessonContaining(
							new Date(System.currentTimeMillis()), dto.getTeacher(), dto.getLesson()
					);
				} else substitutes = this.substituteRepository.findAllByDateAndTeacherAndLessonContaining(
						new Date(dto.getDate()), dto.getTeacher(), dto.getLesson());
			}
		} else if (Validation.validateNotNullOrEmpty(dto.getClassName())) {
			if (dto.getDate() == 0) {
				substitutes = this.substituteRepository.findAllByDateGreaterThanEqualAndTeacherAndClassName(
						new Date(System.currentTimeMillis()), dto.getTeacher(), dto.getClassName()
				);
			} else substitutes = this.substituteRepository.findAllByDateAndTeacherAndClassName(
					new Date(dto.getDate()), dto.getTeacher(), dto.getClassName());
		} else {
			if (dto.getDate() == 0) {
				substitutes = this.substituteRepository.findAllByDateGreaterThanEqualAndTeacher(
						new Date(System.currentTimeMillis()), dto.getTeacher()
				);
			} else substitutes = this.substituteRepository.findAllByDateAndTeacher(
					new Date(dto.getDate()), dto.getTeacher());
		}
		if (substitutes.isEmpty()) return Result.of(Error.NOT_FOUND, NAME_KEY);

		return this.returnSubstitutes(substitutes);
	}

	/**
	 * Get all substitutes by the SubstituteTeacher
	 *
	 * @param dto with information
	 * @return all found substitutes
	 */
	public Result<GetSubstitutesResponseDTO> getSubstitutesBySubstituteTeacher(GetSubstitutesBySubstituteTeacherRequestDTO dto) {
		if (!(DateUtils.isSameDay(new Date(System.currentTimeMillis()), new Date(dto.getDate()))
				|| System.currentTimeMillis() < dto.getDate()) && dto.getDate() != 0)
			return Result.of(Error.INVALID_PARAM, "Date can't be in the past");

		List<SubstituteModel> substitutes;
		if (dto.getDate() == 0) {
			substitutes = this.substituteRepository.findAllByDateGreaterThanEqualAndSubstituteTeacher(
					new Date(System.currentTimeMillis()), dto.getTeacher().toUpperCase());
		} else substitutes = this.substituteRepository.findAllByDateAndSubstituteTeacher(
				new Date(dto.getDate()), dto.getTeacher().toUpperCase());
		if (substitutes.isEmpty()) return Result.of(Error.NOT_FOUND, NAME_KEY);

		return this.returnSubstitutes(substitutes);
	}

	/**
	 * Get all substitutes by a class name
	 *
	 * @param dto with information
	 * @return all found substitutes
	 */
	public Result<GetSubstitutesResponseDTO> getSubstitutesByClassName(GetSubstitutesByClassNameRequestDTO dto) {
		if (!(DateUtils.isSameDay(new Date(System.currentTimeMillis()), new Date(dto.getDate()))
				|| System.currentTimeMillis() < dto.getDate()) && dto.getDate() != 0)
			return Result.of(Error.INVALID_PARAM, "Date can't be in the past");

		List<SubstituteModel> substitutes;
		if (dto.getDate() == 0) {
			if (Character.isDigit(dto.getClassName().charAt(0)))
				substitutes = this.substituteRepository.findAllByDateGreaterThanEqualAndClassNameMatchesRegex(
						new Date(System.currentTimeMillis()),
						dto.getClassName().charAt(0) + "(.*)" + dto.getClassName().charAt(1));//Include like 5abcde
			else substitutes = this.substituteRepository.findAllByDateGreaterThanEqualAndClassName(
					new Date(System.currentTimeMillis()), dto.getClassName().toUpperCase());
		} else {
			if (Character.isDigit(dto.getClassName().charAt(0)))
				substitutes = this.substituteRepository.findAllByDateGreaterThanEqualAndClassNameMatchesRegex(
						new Date(System.currentTimeMillis()),
						dto.getClassName().charAt(0) + "(.*)" + dto.getClassName().charAt(1));//Include like 5abcde
			else substitutes = this.substituteRepository.findAllByDateAndClassName(
					new Date(dto.getDate()), dto.getClassName());
		}
		if (substitutes.isEmpty()) return Result.of(Error.NOT_FOUND, NAME_KEY);

		return this.returnSubstitutes(substitutes);
	}

	/**
	 * Get all substitutes since date
	 *
	 * @param date can't be in the past
	 * @return all found substitutes
	 */
	public Result<GetSubstitutesResponseDTO> getAllSubstitutes(long date) {
		if (!(DateUtils.isSameDay(new Date(System.currentTimeMillis()), new Date(date))
				|| System.currentTimeMillis() < date) && date != 0)
			return Result.of(Error.INVALID_PARAM, "Date can't be in the past");

		List<SubstituteModel> substitutes;
		if (date == 0)
			substitutes = this.substituteRepository.findAllByDateGreaterThanEqual(new Date(System.currentTimeMillis()));
		else substitutes = this.substituteRepository.findAllByDate(new Date(date));
		if (substitutes.isEmpty()) return Result.of(Error.NOT_FOUND, NAME_KEY);

		return this.returnSubstitutes(substitutes);
	}

	/**
	 * Create a {@link SubstituteModel} out of a {@link SubstituteDTO}
	 *
	 * @param dto with information
	 */
	private SubstituteModel createSubstitute(SubstituteDTO dto) {
		return new SubstituteModel(
				-1,
				dto.getDate(),
				dto.getClassName(),
				dto.getLesson(),
				dto.getSubject().toUpperCase(),
				dto.getSubstituteTeacher().toUpperCase(),
				dto.getTeacher().toUpperCase(),
				dto.getType(),
				dto.getSubstituteOf(),
				dto.getRoom(),
				dto.getText()
		);
	}

	/**
	 * Function to convert a list of {@link SubstituteModel} into a list of {@link GetSubstitutesResponseDTO}
	 *
	 * @param substitutes list to convert
	 * @return converted list of {@link GetSubstitutesResponseDTO}
	 */
	private Result<GetSubstitutesResponseDTO> returnSubstitutes(List<SubstituteModel> substitutes) {
		return Result.of(new GetSubstitutesResponseDTO(substitutes.stream()
				.map(SubstituteModel::toResponseDTO).collect(Collectors.toList())));
	}

}
