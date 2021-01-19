package io.github.paexception.engelsburg.api.controller;

import io.github.paexception.engelsburg.api.database.repository.SubstituteMessageRepository;
import io.github.paexception.engelsburg.api.endpoint.dto.request.CreateSubstituteMessageRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.GetSubstituteMessagesResponseDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.SubstituteMessageResponseDTO;
import io.github.paexception.engelsburg.api.database.model.SubstituteMessageModel;
import io.github.paexception.engelsburg.api.util.Error;
import io.github.paexception.engelsburg.api.util.Result;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.transaction.Transactional;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for substitute messages
 */
@Component
public class SubstituteMessageController {

	@Autowired private SubstituteMessageRepository substituteMessageRepository;

	/**
	 * Create a substitute message
	 * Only {@link io.github.paexception.engelsburg.api.service.SubstituteUpdateService} is supposed to call
	 * this function!
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
	 * Clear substitute messages of specific day
	 * Only {@link io.github.paexception.engelsburg.api.service.SubstituteUpdateService} is supposed to call
	 * this function!
	 * @param date day to clear substitute messages
	 */
	@Transactional
	public void clearSubstituteMessages(Date date) {
		this.substituteMessageRepository.deleteByDate(date);
	}

	/**
	 * Return all substitute messages since
	 * @param date can't be in the past
	 * @return all found substitute messages
	 */
	public Result<GetSubstituteMessagesResponseDTO> getAllSubstituteMessages(long date) {
		if (!(DateUtils.isSameDay(new Date(System.currentTimeMillis()), new Date(date))
				|| System.currentTimeMillis()<date) && date!=0)
			return Result.of(Error.INVALID_PARAM, "Date can't be in the past");

		List<SubstituteMessageModel> substitutes;
		if (date==0)
			substitutes = this.substituteMessageRepository.findAllByDateGreaterThanEqual(new Date(System.currentTimeMillis()));
		else substitutes = this.substituteMessageRepository.findAllByDate(new Date(date));
		if (substitutes.isEmpty()) return Result.of(Error.NOT_FOUND, "substitutes");

		List<SubstituteMessageResponseDTO> responseDTOs = new ArrayList<>();
		substitutes.forEach(substituteModel -> responseDTOs.add(substituteModel.toResponseDTO()));

		return Result.of(new GetSubstituteMessagesResponseDTO(responseDTOs));
	}

}
