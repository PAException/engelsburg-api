package io.github.paexception.engelsburginfrastructure.controller;

import io.github.paexception.engelsburginfrastructure.database.model.SubstituteModel;
import io.github.paexception.engelsburginfrastructure.database.repository.SubstituteRepository;
import io.github.paexception.engelsburginfrastructure.endpoint.dto.SubstituteDTO;
import io.github.paexception.engelsburginfrastructure.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class SubstituteController {

    @Autowired private SubstituteRepository substituteRepository;

    public Result<SubstituteModel> createOrUpdateSubstitute(SubstituteDTO dto) {
        Optional<SubstituteModel> optionalSubstitute = substituteRepository
                .findByDateAndLessonAndTeacher(dto.getDate(), dto.getLesson(), dto.getTeacher());

        SubstituteModel substitute;
        if (optionalSubstitute.isEmpty()) substitute = new SubstituteModel(
                -1,
                dto.getDate(),
                dto.getClassName(),
                dto.getLesson(),
                dto.getSubject(),
                dto.getSubstituteTeacher(),
                dto.getTeacher(),
                dto.getType(),
                dto.getSubstituteOf(),
                dto.getRoom(),
                dto.getText()
        );
        else substitute = optionalSubstitute.get()
                .updateDate(dto.getDate())
                .updateClassName(dto.getClassName())
                .updateLesson(dto.getLesson())
                .updateSubject(dto.getSubject())
                .updateSubstituteTeacher(dto.getSubstituteTeacher())
                .updateTeacher(dto.getTeacher())
                .updateType(dto.getType())
                .updateSubstituteOf(dto.getSubstituteOf())
                .updateRoom(dto.getRoom())
                .updateText(dto.getText());

        return Result.of(substituteRepository.save(substitute));
    }

}
