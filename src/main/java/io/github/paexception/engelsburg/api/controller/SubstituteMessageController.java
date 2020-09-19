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

@Component
public class SubstituteMessageController {

	@Autowired private SubstituteMessageRepository substituteMessageRepository;

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

	@Transactional
	public void clearSubstituteMessages(Date date) {
		this.substituteMessageRepository.deleteByDate(date);
	}

	public Result<Object> getAllSubstituteMessages(long date) {
		if (!(DateUtils.isSameDay(new Date(System.currentTimeMillis()), new Date(date))
				|| System.currentTimeMillis()<date) && date!=0)
			return Result.of(Error.INVALID_PARAM, "Date can't be days in the past");

		List<SubstituteMessageModel> substitutes;
		if (date==0)
			substitutes = this.substituteMessageRepository.findAllByDateGreaterThanEqual(new Date(System.currentTimeMillis()));
		else substitutes = this.substituteMessageRepository.findAllByDate(new Date(date));

		List<SubstituteMessageResponseDTO> responseDTOs = new ArrayList<>();
		substitutes.forEach(substituteModel -> responseDTOs.add(substituteModel.toResponseDTO()));

		return Result.of(new GetSubstituteMessagesResponseDTO(responseDTOs));
	}

}
