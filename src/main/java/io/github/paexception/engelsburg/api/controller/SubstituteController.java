package io.github.paexception.engelsburg.api.controller;

import io.github.paexception.engelsburg.api.database.model.SubstituteModel;
import io.github.paexception.engelsburg.api.database.repository.SubstituteRepository;
import io.github.paexception.engelsburg.api.endpoint.dto.request.CreateSubstituteRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.GetSubstitutesByClassNameRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.GetSubstitutesBySubstituteTeacherRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.GetSubstitutesByTeacherRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.GetSubstitutesResponseDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.SubstituteResponseDTO;
import io.github.paexception.engelsburg.api.util.Error;
import io.github.paexception.engelsburg.api.util.Result;
import io.github.paexception.engelsburg.api.util.Validation;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.transaction.Transactional;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for substitutes
 */
@Component
public class SubstituteController {

    @Autowired private SubstituteRepository substituteRepository;

    /**
     * Create a substitute
     * @param dto with information
     */
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

    /**
     * Get all substitutes by Teacher
     * @param dto with information
     * @return all found substitutes
     */
    public Result<GetSubstitutesResponseDTO> getSubstitutesByTeacher(GetSubstitutesByTeacherRequestDTO dto) {
        if (!(DateUtils.isSameDay(new Date(System.currentTimeMillis()), new Date(dto.getDate()))
                || System.currentTimeMillis()<dto.getDate()) && dto.getDate()!=0)
            return Result.of(Error.INVALID_PARAM, "Date can't be in the past");

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
        if (substitutes.isEmpty()) return Result.of(Error.NOT_FOUND, "substitutes");

        return this.returnSubstitutes(substitutes);
    }

    /**
     * Get all substitutes by the SubstituteTeacher
     * @param dto with information
     * @return all found substitutes
     */
    public Result<GetSubstitutesResponseDTO> getSubstitutesBySubstituteTeacher(GetSubstitutesBySubstituteTeacherRequestDTO dto) {
        if (!(DateUtils.isSameDay(new Date(System.currentTimeMillis()), new Date(dto.getDate()))
                || System.currentTimeMillis()<dto.getDate()) && dto.getDate()!=0)
            return Result.of(Error.INVALID_PARAM, "Date can't be in the past");

        List<SubstituteModel> substitutes;
        if (dto.getDate()==0) {
            substitutes = this.substituteRepository.findAllByDateGreaterThanEqualAndSubstituteTeacher(
                    new Date(System.currentTimeMillis()), dto.getTeacher().toUpperCase());
        } else substitutes = this.substituteRepository.findAllByDateAndSubstituteTeacher(
                new Date(dto.getDate()), dto.getTeacher().toUpperCase());
        if (substitutes.isEmpty()) return Result.of(Error.NOT_FOUND, "substitutes");

        return this.returnSubstitutes(substitutes);
    }

    /**
     * Get all substitutes by a class name
     * @param dto with information
     * @return all found substitutes
     */
    public Result<GetSubstitutesResponseDTO> getSubstitutesByClassName(GetSubstitutesByClassNameRequestDTO dto) {
        if (!(DateUtils.isSameDay(new Date(System.currentTimeMillis()), new Date(dto.getDate()))
                || System.currentTimeMillis()<dto.getDate()) && dto.getDate()!=0)
            return Result.of(Error.INVALID_PARAM, "Date can't be in the past");

        List<SubstituteModel> substitutes;
        if (dto.getDate()==0) {
            substitutes = this.substituteRepository.findAllByDateGreaterThanEqualAndClassName(
                    new Date(System.currentTimeMillis()), dto.getClassName().toUpperCase());
        } else substitutes = this.substituteRepository.findAllByDateAndClassName(
                new Date(dto.getDate()), dto.getClassName());
        if (substitutes.isEmpty()) return Result.of(Error.NOT_FOUND, "substitutes");

        return this.returnSubstitutes(substitutes);
    }

    /**
     * Get all substitutes since date
     * @param date can't be in the past
     * @return all found substitutes
     */
    public Result<GetSubstitutesResponseDTO> getAllSubstitutes(long date) {
        if (!(DateUtils.isSameDay(new Date(System.currentTimeMillis()), new Date(date))
                || System.currentTimeMillis()<date) && date!=0)
            return Result.of(Error.INVALID_PARAM, "Date can't be in the past");

        List<SubstituteModel> substitutes;
        if (date==0)
            substitutes = this.substituteRepository.findAllByDateGreaterThanEqual(new Date(System.currentTimeMillis()));
        else substitutes = this.substituteRepository.findAllByDate(new Date(date));
        if (substitutes.isEmpty()) return Result.of(Error.NOT_FOUND, "substitutes");

        return this.returnSubstitutes(substitutes);
    }

    /**
     * Delete all substitutes of date
     */
    @Transactional
    public void clearSubstitutes(Date date) {
        this.substituteRepository.deleteAllByDate(date);
    }

    /**
     * Function to convert a list of {@link SubstituteModel} into a list of {@link GetSubstitutesResponseDTO}
     * @param substitutes list to convert
     * @return converted list of {@link GetSubstitutesResponseDTO}
     */
    private Result<GetSubstitutesResponseDTO> returnSubstitutes(List<SubstituteModel> substitutes) {
        List<SubstituteResponseDTO> responseDTOs = new ArrayList<>();
        substitutes.forEach(substituteModel -> responseDTOs.add(substituteModel.toResponseDTO()));

        return Result.of(new GetSubstitutesResponseDTO(responseDTOs));
    }

}
