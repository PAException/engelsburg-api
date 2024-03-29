/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.controller.reserved;

import io.github.paexception.engelsburg.api.database.model.SubstituteMessageModel;
import io.github.paexception.engelsburg.api.database.repository.SubstituteMessageRepository;
import io.github.paexception.engelsburg.api.endpoint.dto.request.CreateSubstituteMessageRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.GetSubstituteMessagesResponseDTO;
import io.github.paexception.engelsburg.api.service.scheduled.SubstituteUpdateService;
import io.github.paexception.engelsburg.api.util.Error;
import io.github.paexception.engelsburg.api.util.Result;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class SubstituteMessageController {

	private final SubstituteMessageRepository substituteMessageRepository;

	/**
	 * Create a substitute message.
	 * Only {@link SubstituteUpdateService} is supposed to call
	 * this function!
	 *
	 * @param dto with information
	 */
	public void createSubstituteMessage(CreateSubstituteMessageRequestDTO dto) {
		this.substituteMessageRepository.save(new SubstituteMessageModel(
				-1,
				dto.getDate(),
				dto.getAbsenceTeachers(),
				dto.getAbsenceClasses(),
				dto.getAffectedClasses(),
				dto.getAffectedRooms(),
				dto.getBlockedRooms(),
				dto.getMessages()
		));
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
	 * @return all found substitute messages
	 */
	public Result<GetSubstituteMessagesResponseDTO> getAllSubstituteMessages() {
		//Get substitute messages by date, if not present return error
		List<SubstituteMessageModel> substitutes = this.substituteMessageRepository.findAllByDateGreaterThanEqual(
				new Date(System.currentTimeMillis()));
		if (substitutes.isEmpty()) return Result.of(Error.NOT_FOUND, NAME_KEY);

		//Map and return substitute messages
		return Result.of(new GetSubstituteMessagesResponseDTO(substitutes.stream()
				.map(SubstituteMessageModel::toResponseDTO).collect(Collectors.toList())));
	}
}
