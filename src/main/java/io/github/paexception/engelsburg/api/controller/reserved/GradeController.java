package io.github.paexception.engelsburg.api.controller.reserved;

import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.paexception.engelsburg.api.controller.userdata.UserDataHandler;
import io.github.paexception.engelsburg.api.database.model.GradeModel;
import io.github.paexception.engelsburg.api.database.repository.GradeRepository;
import io.github.paexception.engelsburg.api.endpoint.dto.GradeDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.CreateGradeRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.UpdateGradeRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.GetGradesResponseDTO;
import io.github.paexception.engelsburg.api.util.Error;
import io.github.paexception.engelsburg.api.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import static io.github.paexception.engelsburg.api.util.Constants.Grade.NAME_KEY;

/**
 * Controller for grades.
 */
@Component
public class GradeController implements UserDataHandler {

	@Autowired
	private GradeRepository gradeRepository;

	/**
	 * Create new grade.
	 *
	 * @param dto with grade information
	 * @param jwt with userId
	 * @return created grade
	 */
	public Result<GradeDTO> createGrade(CreateGradeRequestDTO dto, DecodedJWT jwt) {
		return Result.of(this.gradeRepository.save(new GradeModel(
				-1,
				UUID.fromString(jwt.getSubject()),
				dto.getName(),
				dto.getShare(),
				dto.getValue(),
				dto.getSubject()
		)).toResponseDTO());
	}

	/**
	 * Update an existing grade.
	 *
	 * @param dto with gradeId and information to change
	 * @param jwt with userId
	 * @return updated grade
	 */
	public Result<GradeDTO> updateGrade(UpdateGradeRequestDTO dto, DecodedJWT jwt) {
		UUID userId = UUID.fromString(jwt.getSubject());
		if (dto.getGradeId() < 0) return Result.of(Error.INVALID_PARAM, NAME_KEY);

		Optional<GradeModel> optionalGrade = this.gradeRepository.findById(dto.getGradeId());
		if (optionalGrade.isEmpty()) return Result.of(Error.NOT_FOUND, NAME_KEY);

		GradeModel grade = optionalGrade.get();
		if (!userId.equals(grade.getUserId())) return Result.of(Error.FORBIDDEN, NAME_KEY);

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
	 * @param jwt     with userId
	 * @return list of grades
	 */
	@Transactional
	public Result<GetGradesResponseDTO> getGrades(String subject, DecodedJWT jwt) {
		UUID userId = UUID.fromString(jwt.getSubject());

		List<GradeDTO> dtos;
		if (subject != null) dtos = this.gradeRepository
				.findAllByUserIdAndSubject(userId, subject).map(GradeModel::toResponseDTO).collect(Collectors.toList());
		else dtos = this.gradeRepository.findAllByUserId(userId).stream()
				.map(GradeModel::toResponseDTO).collect(Collectors.toList());

		if (dtos.isEmpty()) return Result.of(Error.NOT_FOUND, NAME_KEY);
		else return Result.of(new GetGradesResponseDTO(dtos));
	}

	/**
	 * Delete grade.
	 *
	 * @param gradeId to delete
	 * @param jwt     with userId
	 * @return empty result
	 */
	@Transactional
	public Result<?> deleteGrade(int gradeId, DecodedJWT jwt) {
		UUID userId = UUID.fromString(jwt.getSubject());
		Optional<GradeModel> optionalGrade = this.gradeRepository.findById(gradeId);
		if (optionalGrade.isEmpty()) return Result.of(Error.NOT_FOUND, NAME_KEY);

		GradeModel grade = optionalGrade.get();
		if (!grade.getUserId().equals(userId)) return Result.of(Error.FORBIDDEN, NAME_KEY);
		else {
			this.gradeRepository.delete(grade);
			return Result.empty();
		}
	}

	@Override
	public void deleteUserData(UUID userId) {
		this.gradeRepository.deleteAllByUserId(userId);
	}

	@Override
	public Object[] getUserData(UUID userId) {
		return this.mapData(this.gradeRepository.findAllByUserId(userId));
	}

}
