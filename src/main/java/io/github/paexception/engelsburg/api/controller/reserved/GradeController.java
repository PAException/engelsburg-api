package io.github.paexception.engelsburg.api.controller.reserved;

import io.github.paexception.engelsburg.api.controller.userdata.UserDataHandler;
import io.github.paexception.engelsburg.api.database.model.GradeModel;
import io.github.paexception.engelsburg.api.database.model.UserModel;
import io.github.paexception.engelsburg.api.database.repository.GradeRepository;
import io.github.paexception.engelsburg.api.endpoint.dto.GradeDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.UserDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.CreateGradeRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.UpdateGradeRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.GetGradesResponseDTO;
import io.github.paexception.engelsburg.api.util.Error;
import io.github.paexception.engelsburg.api.util.Result;
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
public class GradeController implements UserDataHandler {

	private final GradeRepository gradeRepository;

	public GradeController(GradeRepository gradeRepository) {
		this.gradeRepository = gradeRepository;
	}

	/**
	 * Create new grade.
	 *
	 * @param dto     with grade information
	 * @param userDTO with userId
	 * @return created grade
	 */
	public Result<GradeDTO> createGrade(CreateGradeRequestDTO dto, UserDTO userDTO) {
		return Result.of(this.gradeRepository.save(new GradeModel(
				-1,
				userDTO.user,
				dto.getName(),
				dto.getShare(),
				dto.getValue(),
				dto.getSubject()
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
		if (dto.getGradeId() < 0) return Result.of(Error.INVALID_PARAM, NAME_KEY);

		Optional<GradeModel> optionalGrade = this.gradeRepository.findById(dto.getGradeId());
		if (optionalGrade.isEmpty()) return Result.of(Error.NOT_FOUND, NAME_KEY);

		GradeModel grade = optionalGrade.get();
		if (!grade.getUser().is(userDTO)) return Result.of(Error.FORBIDDEN, NAME_KEY);

		if (dto.getName() != null && !dto.getName().isBlank()) grade.setName(dto.getName());
		if (dto.getShare() >= 0) grade.setShare(dto.getShare());
		if (dto.getValue() >= 0) grade.setValue(dto.getValue());
		if (dto.getSubject() != null && !dto.getSubject().isBlank()) grade.setSubject(dto.getSubject());

		return Result.of(this.gradeRepository.save(grade).toResponseDTO());
	}

	/**
	 * Get all grades of user or filter by subject.
	 *
	 * @param subject (optional)
	 * @param userDTO with userId
	 * @return list of grades
	 */
	@Transactional
	public Result<GetGradesResponseDTO> getGrades(String subject, UserDTO userDTO) {
		List<GradeDTO> dtos;
		if (subject != null) dtos = this.gradeRepository
				.findAllByUserAndSubject(userDTO.user, subject).map(GradeModel::toResponseDTO).collect(
						Collectors.toList());
		else dtos = this.gradeRepository.findAllByUser(userDTO.user).stream()
				.map(GradeModel::toResponseDTO).collect(Collectors.toList());

		if (dtos.isEmpty()) return Result.of(Error.NOT_FOUND, NAME_KEY);
		else return Result.of(new GetGradesResponseDTO(dtos));
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
		Optional<GradeModel> optionalGrade = this.gradeRepository.findById(gradeId);
		if (optionalGrade.isEmpty()) return Result.of(Error.NOT_FOUND, NAME_KEY);

		GradeModel grade = optionalGrade.get();
		if (!grade.getUser().is(userDTO)) return Result.of(Error.FORBIDDEN, NAME_KEY);
		else {
			this.gradeRepository.delete(grade);
			return Result.empty();
		}
	}

	@Override
	public void deleteUserData(UserModel user) {
		this.gradeRepository.deleteAllByUser(user);
	}

	@Override
	public Object[] getUserData(UserModel user) {
		return this.mapData(this.gradeRepository.findAllByUser(user));
	}

}
