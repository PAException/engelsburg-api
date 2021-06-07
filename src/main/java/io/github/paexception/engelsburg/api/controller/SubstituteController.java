package io.github.paexception.engelsburg.api.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.paexception.engelsburg.api.database.model.SubstituteModel;
import io.github.paexception.engelsburg.api.database.repository.SubstituteRepository;
import io.github.paexception.engelsburg.api.endpoint.dto.SubstituteDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.GetSubstitutesByClassNameRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.GetSubstitutesBySubstituteTeacherRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.GetSubstitutesByTeacherRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.GetSubstitutesResponseDTO;
import io.github.paexception.engelsburg.api.service.notification.NotificationService;
import io.github.paexception.engelsburg.api.service.scheduled.SubstituteUpdateService;
import io.github.paexception.engelsburg.api.util.Error;
import io.github.paexception.engelsburg.api.util.Result;
import io.github.paexception.engelsburg.api.util.Validation;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.transaction.Transactional;
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
	 * Only {@link SubstituteUpdateService} is supposed to call
	 * this function!
	 *
	 * @param fetchedDTOs with all crawled substitutes
	 */
	@Transactional
	public void updateSubstitutes(List<SubstituteDTO> fetchedDTOs, Date date) {
		this.substituteRepository.findAllByDate(date).stream().map(SubstituteModel::toResponseDTO)
				.forEach(dto -> fetchedDTOs.removeIf(fetchedDTO -> fetchedDTO.equals(dto)));//Filter new or changed substitutes
		this.notificationService.sendSubstituteNotifications(fetchedDTOs);//Send notifications

		fetchedDTOs.forEach(dto -> {
			if (Character.isDigit(dto.getClassName().charAt(0))) {//5a-10e
				this.substituteRepository.deleteAllByDateAndLessonAndClassNameIsLike(
						date, dto.getLesson(), SubstituteRepository.likeClassName(dto.getClassName()));
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
	public Result<GetSubstitutesResponseDTO> getSubstitutesByTeacher(GetSubstitutesByTeacherRequestDTO dto, DecodedJWT jwt) {
		if (dto.getDate() < 0) dto.setDate(System.currentTimeMillis());
		if (!this.pastTimeCheck(jwt, dto.getDate())) return Result.of(Error.FORBIDDEN, NAME_KEY);

		List<SubstituteModel> substitutes;
		if (dto.getLesson() != -1) {
			if (Validation.validateNotNullOrEmpty(dto.getClassName())) {
				substitutes = this.substituteRepository.findAllByDateAndTeacherAndLessonAndClassName(
						new Date(dto.getDate()), dto.getTeacher(), dto.getLesson(), dto.getClassName());
			} else {
				substitutes = this.substituteRepository.findAllByDateAndTeacherAndLesson(
						new Date(dto.getDate()), dto.getTeacher(), dto.getLesson());
			}
		} else if (Validation.validateNotNullOrEmpty(dto.getClassName())) {
			substitutes = this.substituteRepository.findAllByDateAndTeacherAndClassName(
					new Date(dto.getDate()), dto.getTeacher(), dto.getClassName());
		} else {
			substitutes = this.substituteRepository.findAllByDateAndTeacher(
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
	public Result<GetSubstitutesResponseDTO> getSubstitutesBySubstituteTeacher(GetSubstitutesBySubstituteTeacherRequestDTO dto, DecodedJWT jwt) {
		if (dto.getDate() < 0) dto.setDate(System.currentTimeMillis());
		if (!this.pastTimeCheck(jwt, dto.getDate())) return Result.of(Error.FORBIDDEN, NAME_KEY);

		List<SubstituteModel> substitutes = this.substituteRepository.findAllByDateAndSubstituteTeacher(
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
	public Result<GetSubstitutesResponseDTO> getSubstitutesByClassName(GetSubstitutesByClassNameRequestDTO dto, DecodedJWT jwt) {
		if (dto.getDate() < 0) dto.setDate(System.currentTimeMillis());
		if (!this.pastTimeCheck(jwt, dto.getDate())) return Result.of(Error.FORBIDDEN, NAME_KEY);

		List<SubstituteModel> substitutes;
		if (Character.isDigit(dto.getClassName().charAt(0)))
			substitutes = this.substituteRepository.findAllByDateGreaterThanEqualAndClassNameIsLike(
					new Date(dto.getDate()), SubstituteRepository.likeClassName(dto.getClassName()));//Include like 5abcde
		else substitutes = this.substituteRepository.findAllByDateAndClassName(
				new Date(dto.getDate()), dto.getClassName());
		if (substitutes.isEmpty()) return Result.of(Error.NOT_FOUND, NAME_KEY);

		return this.returnSubstitutes(substitutes);
	}

	/**
	 * Get all substitutes since date
	 *
	 * @param date can't be in the past
	 * @return all found substitutes
	 */
	public Result<GetSubstitutesResponseDTO> getAllSubstitutes(long date, DecodedJWT jwt) {
		if (date < 0) date = System.currentTimeMillis();
		if (!this.pastTimeCheck(jwt, date)) return Result.of(Error.FORBIDDEN, NAME_KEY);

		List<SubstituteModel> substitutes = this.substituteRepository.findAllByDateGreaterThanEqual(new Date(date));
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
				dto.getSubject() != null ? dto.getSubject().toUpperCase() : null,
				dto.getSubstituteTeacher() != null ? dto.getSubstituteTeacher().toUpperCase() : null,
				dto.getTeacher() != null ? dto.getTeacher().toUpperCase() : null,
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

	/**
	 * Checks if sender has permission to get past substitutes
	 *
	 * @param jwt  with scopes
	 * @param date specified
	 * @return true if permitted, false if not
	 */
	private boolean pastTimeCheck(DecodedJWT jwt, long date) {
		if (!jwt.getClaim("scopes").asList(String.class).contains("substitute.read.all")) {
			return DateUtils.isSameDay(new Date(System.currentTimeMillis()), new Date(date)) || System.currentTimeMillis() <= date;//Same day or in the future
		}

		return true;
	}

}
