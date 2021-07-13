package io.github.paexception.engelsburg.api.controller.reserved;

import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.paexception.engelsburg.api.database.model.SubstituteMessageModel;
import io.github.paexception.engelsburg.api.database.repository.SubstituteMessageRepository;
import io.github.paexception.engelsburg.api.endpoint.dto.request.CreateSubstituteMessageRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.GetSubstituteMessagesResponseDTO;
import io.github.paexception.engelsburg.api.service.scheduled.SubstituteUpdateService;
import io.github.paexception.engelsburg.api.util.Constants;
import io.github.paexception.engelsburg.api.util.Error;
import io.github.paexception.engelsburg.api.util.Result;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.transaction.Transactional;
import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;
import static io.github.paexception.engelsburg.api.util.Constants.SubstituteMessage.NAME_KEY;

/**
 * Controller for substitute messages.
 */
@Component
public class SubstituteMessageController {

	@Autowired
	private SubstituteMessageRepository substituteMessageRepository;

	/**
	 * Checks if sender has permission to get past substitutes messages.
	 *
	 * @param jwt  with scopes
	 * @param date specified
	 * @return true if permitted, false if not
	 */
	private static boolean pastTimeCheck(DecodedJWT jwt, long date) {
		if (!jwt.getClaim("scopes").asList(String.class).contains("substitute.message.read.all")) {
			return DateUtils.isSameDay(new Date(System.currentTimeMillis()), new Date(date)) || System.currentTimeMillis() <= date; //Same day or in the future
		} else return true;
	}

	/**
	 * Create a substitute message.
	 * Only {@link SubstituteUpdateService} is supposed to call
	 * this function!
	 *
	 * @param dto with information
	 */
	public void createSubstituteMessage(CreateSubstituteMessageRequestDTO dto) {
		SubstituteMessageModel substituteMessage = new SubstituteMessageModel(
				-1,
				dto.getDate(),
				dto.getAbsenceTeachers(),
				dto.getAbsenceClasses(),
				dto.getAffectedClasses(),
				dto.getAffectedRooms(),
				dto.getBlockedRooms(),
				dto.getMessages()
		);

		this.substituteMessageRepository.save(substituteMessage);
	}

	/**
	 * Clear substitute messages of specific day.
	 * Only {@link SubstituteUpdateService} is supposed to call
	 * this function!
	 *
	 * @param date day to clear substitute messages
	 */
	@Transactional
	public void clearSubstituteMessages(Date date) {
		this.substituteMessageRepository.deleteByDate(date);
	}

	/**
	 * Return all substitute messages since.
	 *
	 * @param date can't be in the past
	 * @param jwt  to check permissions
	 * @return all found substitute messages
	 */
	public Result<GetSubstituteMessagesResponseDTO> getAllSubstituteMessages(long date, DecodedJWT jwt) {
		if (date < 0) date = System.currentTimeMillis();
		if (!pastTimeCheck(jwt, date)) return Result.of(Error.FORBIDDEN, Constants.Substitute.NAME_KEY);

		List<SubstituteMessageModel> substitutes = this.substituteMessageRepository.findAllByDateGreaterThanEqual(new Date(System.currentTimeMillis()));
		if (substitutes.isEmpty()) return Result.of(Error.NOT_FOUND, NAME_KEY);
		else return Result.of(new GetSubstituteMessagesResponseDTO(substitutes.stream()
				.map(SubstituteMessageModel::toResponseDTO).collect(Collectors.toList())));
	}

}
