package io.github.paexception.engelsburg.api.controller.reserved;

import io.github.paexception.engelsburg.api.database.model.SubstituteMessageModel;
import io.github.paexception.engelsburg.api.database.repository.SubstituteMessageRepository;
import io.github.paexception.engelsburg.api.endpoint.dto.UserDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.CreateSubstituteMessageRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.GetSubstituteMessagesResponseDTO;
import io.github.paexception.engelsburg.api.service.scheduled.SubstituteUpdateService;
import io.github.paexception.engelsburg.api.util.Constants;
import io.github.paexception.engelsburg.api.util.Error;
import io.github.paexception.engelsburg.api.util.Result;
import org.apache.commons.lang3.time.DateUtils;
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

	private final SubstituteMessageRepository substituteMessageRepository;

	public SubstituteMessageController(
			SubstituteMessageRepository substituteMessageRepository) {
		this.substituteMessageRepository = substituteMessageRepository;
	}

	/**
	 * Checks if sender has permission to get past substitutes messages.
	 *
	 * @param userDTO user information
	 * @param date    specified
	 * @return true if permitted, false if not
	 */
	private static boolean pastTimeCheck(UserDTO userDTO, long date) {
		if (!userDTO.hasScope("substitute.message.read.all")) {
			return DateUtils.isSameDay(new Date(System.currentTimeMillis()),
					new Date(date)) || System.currentTimeMillis() <= date; //Same day or in the future
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
	 * @param date    can't be in the past
	 * @param userDTO user information
	 * @return all found substitute messages
	 */
	public Result<GetSubstituteMessagesResponseDTO> getAllSubstituteMessages(long date, UserDTO userDTO) {
		if (date < 0) date = System.currentTimeMillis();
		if (!pastTimeCheck(userDTO, date)) return Result.of(Error.FORBIDDEN, Constants.Substitute.NAME_KEY);

		List<SubstituteMessageModel> substitutes = this.substituteMessageRepository.findAllByDateGreaterThanEqual(
				new Date(date));
		if (substitutes.isEmpty()) return Result.of(Error.NOT_FOUND, NAME_KEY);
		else return Result.of(new GetSubstituteMessagesResponseDTO(substitutes.stream()
				.map(SubstituteMessageModel::toResponseDTO).collect(Collectors.toList())));
	}

}
