package io.github.paexception.engelsburginfrastructure.controller;

import io.github.paexception.engelsburginfrastructure.database.model.SubstituteModel;
import io.github.paexception.engelsburginfrastructure.database.repository.SubstituteRepository;
import io.github.paexception.engelsburginfrastructure.endpoint.dto.request.CreateSubstituteRequestDTO;
import io.github.paexception.engelsburginfrastructure.endpoint.dto.request.GetSubstitutesByClassNameRequestDTO;
import io.github.paexception.engelsburginfrastructure.endpoint.dto.request.GetSubstitutesBySubstituteTeacherRequestDTO;
import io.github.paexception.engelsburginfrastructure.endpoint.dto.request.GetSubstitutesByTeacherRequestDTO;
import io.github.paexception.engelsburginfrastructure.endpoint.dto.response.GetSubstitutesResponseDTO;
import io.github.paexception.engelsburginfrastructure.endpoint.dto.response.SubstituteResponseDTO;
import io.github.paexception.engelsburginfrastructure.util.Error;
import io.github.paexception.engelsburginfrastructure.util.Result;
import io.github.paexception.engelsburginfrastructure.util.Validation;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.transaction.Transactional;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Component
public class SubstituteController {

    @Autowired private SubstituteRepository substituteRepository;

    public void createSubstitute(CreateSubstituteRequestDTO dto) {
        SubstituteModel substitute = new SubstituteModel(
                -1,
                dto.getDate(),
                dto.getClassName(),
                dto.getLesson(),
                dto.getSubject(),
                dto.getSubstituteTeacher(),
                dto.getTeacher().toUpperCase(),
                dto.getType(),
                dto.getSubstituteOf(),
                dto.getRoom(),
                dto.getText()
        );

       this.substituteRepository.save(substitute);
    }

    public Result<GetSubstitutesResponseDTO> getSubstitutesByTeacher(GetSubstitutesByTeacherRequestDTO dto) {
        if (!(DateUtils.isSameDay(new Date(System.currentTimeMillis()), new Date(dto.getDate()))
                || System.currentTimeMillis()<dto.getDate()) && dto.getDate()!=0)
            return Result.of(Error.INVALID_PARAM, "Date can't be days in the past");

        List<SubstituteModel> substitutes;
        if (Validation.validateNotNullOrEmpty(dto.getLesson())) {
            if (Validation.validateNotNullOrEmpty(dto.getClassName())) {
                if (dto.getDate()==0) {
                    substitutes = this.substituteRepository.findAllByDateGreaterThanEqualAndTeacherAndLessonContainingAndClassName(
                            new Date(System.currentTimeMillis()), dto.getTeacher(), dto.getLesson(), dto.getClassName()
                    );
                }else substitutes = this.substituteRepository.findAllByDateAndTeacherAndLessonContainingAndClassName(
                        new Date(dto.getDate()), dto.getTeacher(), dto.getLesson(), dto.getClassName());
            } else {
                if (dto.getDate()==0) {
                    substitutes = this.substituteRepository.findAllByDateGreaterThanEqualAndTeacherAndLessonContaining(
                            new Date(System.currentTimeMillis()), dto.getTeacher(), dto.getLesson()
                    );
                }else substitutes = this.substituteRepository.findAllByDateAndTeacherAndLessonContaining(
                        new Date(dto.getDate()), dto.getTeacher(), dto.getLesson());
            }
        } else if (Validation.validateNotNullOrEmpty(dto.getClassName())) {
            if (dto.getDate()==0) {
                substitutes = this.substituteRepository.findAllByDateGreaterThanEqualAndTeacherAndClassName(
                        new Date(System.currentTimeMillis()), dto.getTeacher(), dto.getClassName()
                );
            } else substitutes = this.substituteRepository.findAllByDateAndTeacherAndClassName(
                    new Date(dto.getDate()), dto.getTeacher(), dto.getClassName());
        } else {
            if (dto.getDate()==0) {
                substitutes = this.substituteRepository.findAllByDateGreaterThanEqualAndTeacher(
                        new Date(System.currentTimeMillis()), dto.getTeacher()
                );
            } else substitutes = this.substituteRepository.findAllByDateAndTeacher(
                    new Date(dto.getDate()), dto.getTeacher());
        }

        return this.returnSubstitutes(substitutes);
    }

    public Result<GetSubstitutesResponseDTO> getSubstitutesBySubstituteTeacher(GetSubstitutesBySubstituteTeacherRequestDTO dto) {
        if (!(DateUtils.isSameDay(new Date(System.currentTimeMillis()), new Date(dto.getDate()))
                || System.currentTimeMillis()<dto.getDate()) && dto.getDate()!=0)
            return Result.of(Error.INVALID_PARAM, "Date can't be days in the past");

        List<SubstituteModel> substitutes;
        if (dto.getDate()==0) {
            substitutes = this.substituteRepository.findAllByDateGreaterThanEqualAndSubstituteTeacher(
                    new Date(System.currentTimeMillis()), dto.getTeacher().toUpperCase());
        } else substitutes = this.substituteRepository.findAllByDateAndSubstituteTeacher(
                new Date(dto.getDate()), dto.getTeacher().toUpperCase());

        return this.returnSubstitutes(substitutes);
    }

    public Result<GetSubstitutesResponseDTO> getSubstitutesByClassName(GetSubstitutesByClassNameRequestDTO dto) {
        if (!(DateUtils.isSameDay(new Date(System.currentTimeMillis()), new Date(dto.getDate()))
                || System.currentTimeMillis()<dto.getDate()) && dto.getDate()!=0)
            return Result.of(Error.INVALID_PARAM, "Date can't be days in the past");

        List<SubstituteModel> substitutes;
        if (dto.getDate()==0) {
            substitutes = this.substituteRepository.findAllByDateGreaterThanEqualAndClassName(
                    new Date(System.currentTimeMillis()), dto.getClassName().toUpperCase());
        } else substitutes = this.substituteRepository.findAllByDateAndClassName(
                new Date(dto.getDate()), dto.getClassName());

        return this.returnSubstitutes(substitutes);
    }

    public Result<GetSubstitutesResponseDTO> getAllSubstitutes(long date) {
        if (!(DateUtils.isSameDay(new Date(System.currentTimeMillis()), new Date(date))
                || System.currentTimeMillis()<date) && date!=0)
            return Result.of(Error.INVALID_PARAM, "Date can't be days in the past");

        List<SubstituteModel> substitutes;
        if (date==0)
            substitutes = this.substituteRepository.findAllByDateGreaterThanEqual(new Date(System.currentTimeMillis()));
        else substitutes = this.substituteRepository.findAllByDate(new Date(date));

        return this.returnSubstitutes(substitutes);
    }

    @Transactional
    public void clearSubstitutes(Date date) {
        this.substituteRepository.deleteAllByDate(date);
    }

    private Result<GetSubstitutesResponseDTO> returnSubstitutes(List<SubstituteModel> substitutes) {
        List<SubstituteResponseDTO> responseDTOs = new ArrayList<>();
        substitutes.forEach(substituteModel -> responseDTOs.add(substituteModel.toResponseDTO()));

        return Result.of(new GetSubstitutesResponseDTO(responseDTOs));
    }

}
