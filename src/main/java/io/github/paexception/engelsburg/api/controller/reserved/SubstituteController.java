package io.github.paexception.engelsburg.api.controller.reserved;

import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.paexception.engelsburg.api.database.model.SubstituteModel;
import io.github.paexception.engelsburg.api.database.repository.SubstituteRepository;
import io.github.paexception.engelsburg.api.endpoint.dto.SubstituteDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.GetSubstitutesResponseDTO;
import io.github.paexception.engelsburg.api.service.notification.NotificationService;
import io.github.paexception.engelsburg.api.service.scheduled.SubstituteUpdateService;
import io.github.paexception.engelsburg.api.util.Error;
import io.github.paexception.engelsburg.api.util.Result;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.transaction.Transactional;
import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import static io.github.paexception.engelsburg.api.util.Constants.Substitute.NAME_KEY;

/**
 * Controller for substitutes.
 */
@Component
public class SubstituteController {

	@Autowired
	private SubstituteRepository substituteRepository;
	@Autowired
	private NotificationService notificationService;

	/**
	 * Checks if sender has permission to get past substitutes.
	 *
	 * @param jwt  with scopes
	 * @param date specified
	 * @return true if permitted, false if not
	 */
	private static boolean pastTimeCheck(DecodedJWT jwt, long date) {
		if (!jwt.getClaim("scopes").asList(String.class).contains("substitute.read.all")) {
			return DateUtils.isSameDay(new Date(System.currentTimeMillis()), new Date(date)) || System.currentTimeMillis() <= date; //Same day or in the future
		} else return true;
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
	 * Update substitutes.
	 * Only {@link SubstituteUpdateService} is supposed to call
	 * this function!
	 *
	 * @param fetchedDTOs with all crawled substitutes
	 * @param date        of substitutes
	 */
	@Transactional
	public void updateSubstitutes(List<SubstituteDTO> fetchedDTOs, Date date) {
		this.substituteRepository.findAllByDate(date).stream().map(SubstituteModel::toResponseDTO)
				.forEach(dto -> fetchedDTOs.removeIf(fetchedDTO -> fetchedDTO.equals(dto))); //Filter new or changed substitutes
		fetchedDTOs.removeIf(dto -> {
			if (Character.isDigit(dto.getClassName().charAt(0))) { //5a-10e
				Optional<SubstituteModel> optionalSubstitute = this.substituteRepository.findByDateAndLessonAndClassNameIsLike(
						date, dto.getLesson(), SubstituteRepository.likeClassName(dto.getClassName()));
				if (optionalSubstitute.isPresent()) {
					this.substituteRepository.save(this.createSubstitute(optionalSubstitute.get().getSubstituteId(), dto));
					return true;
				}
			} else { //E1-Q4
				if (dto.getTeacher().isBlank()) {
					Optional<SubstituteModel> optionalSubstitute = this.substituteRepository.findByDateAndLessonAndSubject(
							date, dto.getLesson(), dto.getSubject());
					if (optionalSubstitute.isPresent()) {
						this.substituteRepository.save(this.createSubstitute(optionalSubstitute.get().getSubstituteId(), dto));
						return true;
					}
				} else {
					Optional<SubstituteModel> optionalSubstitute = this.substituteRepository.findByDateAndLessonAndTeacher(
							date, dto.getLesson(), dto.getTeacher());
					if (optionalSubstitute.isPresent()) {
						this.substituteRepository.save(this.createSubstitute(optionalSubstitute.get().getSubstituteId(), dto));
						return true;
					}
				}
			}

			this.substituteRepository.save(this.createSubstitute(-1, dto));
			return false;
		});

		if (!fetchedDTOs.isEmpty()) this.notificationService.sendSubstituteNotifications(fetchedDTOs);
	}

	/**
	 * Get all substitutes by Teacher.
	 *
	 * @param teacher   filter by teacher
	 * @param lesson    filter by lesson
	 * @param className filter by className
	 * @param date      filter by date
	 * @param jwt       with userId
	 * @return all found substitutes
	 */
	public Result<GetSubstitutesResponseDTO> getSubstitutesByTeacher(String teacher, int lesson, String className, long date, DecodedJWT jwt) {
		if (date < 0) date = System.currentTimeMillis();
		if (!pastTimeCheck(jwt, date)) return Result.of(Error.FORBIDDEN, NAME_KEY);

		List<SubstituteModel> substitutes;
		if (lesson != -1) {
			if (notBlank(className)) {
				substitutes = this.substituteRepository.findAllByDateAndTeacherAndLessonAndClassName(
						new Date(date), teacher, lesson, className);
			} else {
				substitutes = this.substituteRepository.findAllByDateAndTeacherAndLesson(
						new Date(date), teacher, lesson);
			}
		} else if (notBlank(className)) {
			substitutes = this.substituteRepository.findAllByDateAndTeacherAndClassName(
					new Date(date), teacher, className);
		} else {
			substitutes = this.substituteRepository.findAllByDateAndTeacher(
					new Date(date), teacher);
		}
		if (substitutes.isEmpty()) return Result.of(Error.NOT_FOUND, NAME_KEY);

		return this.returnSubstitutes(substitutes);
	}

	/**
	 * Get all substitutes by the SubstituteTeacher.
	 *
	 * @param teacher filter by substitute teacher
	 * @param date    filter by date
	 * @param jwt     with userId
	 * @return all found substitutes
	 */
	public Result<GetSubstitutesResponseDTO> getSubstitutesBySubstituteTeacher(String teacher, long date, DecodedJWT jwt) {
		if (date < 0) date = System.currentTimeMillis();
		if (!pastTimeCheck(jwt, date)) return Result.of(Error.FORBIDDEN, NAME_KEY);

		List<SubstituteModel> substitutes = this.substituteRepository.findAllByDateAndSubstituteTeacher(
				new Date(date), teacher.toUpperCase());
		if (substitutes.isEmpty()) return Result.of(Error.NOT_FOUND, NAME_KEY);

		return this.returnSubstitutes(substitutes);
	}

	/**
	 * Get all substitutes by a class name.
	 *
	 * @param className filter by className
	 * @param date      filter by date
	 * @param jwt       with userId
	 * @return all found substitutes
	 */
	public Result<GetSubstitutesResponseDTO> getSubstitutesByClassName(String className, long date, DecodedJWT jwt) {
		if (date < 0) date = System.currentTimeMillis();
		if (!pastTimeCheck(jwt, date)) return Result.of(Error.FORBIDDEN, NAME_KEY);

		List<SubstituteModel> substitutes;
		if (Character.isDigit(className.charAt(0)))
			substitutes = this.substituteRepository.findAllByDateGreaterThanEqualAndClassNameIsLike(
					new Date(date), SubstituteRepository.likeClassName(className)); //Include like 5abcde
		else substitutes = this.substituteRepository.findAllByDateAndClassName(
				new Date(date), className);
		if (substitutes.isEmpty()) return Result.of(Error.NOT_FOUND, NAME_KEY);

		return this.returnSubstitutes(substitutes);
	}

	/**
	 * Get all substitutes since date.
	 *
	 * @param date can't be in the past
	 * @param jwt  with userId
	 * @return all found substitutes
	 */
	public Result<GetSubstitutesResponseDTO> getAllSubstitutes(long date, DecodedJWT jwt) {
		if (date < 0) date = System.currentTimeMillis();
		if (!pastTimeCheck(jwt, date)) return Result.of(Error.FORBIDDEN, NAME_KEY);

		List<SubstituteModel> substitutes = this.substituteRepository.findAllByDateGreaterThanEqual(new Date(date));
		if (substitutes.isEmpty()) return Result.of(Error.NOT_FOUND, NAME_KEY);

		return this.returnSubstitutes(substitutes);
	}

	/**
	 * Create a {@link SubstituteModel} out of a {@link SubstituteDTO}.
	 *
	 * @param substituteId id of substitute
	 * @param dto          with information
	 * @return created substitute model
	 */
	private SubstituteModel createSubstitute(int substituteId, SubstituteDTO dto) {
		return new SubstituteModel(
				substituteId,
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
	 * Function to convert a list of {@link SubstituteModel} into a list of {@link GetSubstitutesResponseDTO}.
	 *
	 * @param substitutes list to convert
	 * @return converted list of {@link GetSubstitutesResponseDTO}
	 */
	private Result<GetSubstitutesResponseDTO> returnSubstitutes(List<SubstituteModel> substitutes) {
		return Result.of(new GetSubstitutesResponseDTO(substitutes.stream()
				.map(SubstituteModel::toResponseDTO).collect(Collectors.toList())));
	}

}
