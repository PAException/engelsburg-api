package io.github.paexception.engelsburg.api.endpoint;

import io.github.paexception.engelsburg.api.controller.SubstituteController;
import io.github.paexception.engelsburg.api.endpoint.dto.request.GetSubstitutesByClassNameRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.GetSubstitutesBySubstituteTeacherRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.GetSubstitutesByTeacherRequestDTO;
import io.github.paexception.engelsburg.api.spring.interceptor.ServiceToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import javax.validation.constraints.Min;

/**
 * RestController for substitute actions
 */
@ServiceToken
@Validated
@RestController
public class SubstituteEndpoint {

    @Autowired private SubstituteController substituteController;

    /**
     * Get all substitutes since specific date
     * @param date can't be in the past
     * @return found substitutes
     */
    @GetMapping("/substitute")
    public Object getAllSubstitutes(@RequestParam(required = false, defaultValue = "0") @Min(0) long date) {
        return this.substituteController.getAllSubstitutes(date).getHttpResponse();
    }

    /**
     * <b>Just returns all substitutes of the day and future</b>
     *
     * @param dto information and filters to get substitutes
     * @return found substitutes
     */
    @GetMapping("/substitute/className")
    public Object getSubstitutesByClassName(@RequestBody @Valid GetSubstitutesByClassNameRequestDTO dto) {
        return this.substituteController.getSubstitutesByClassName(dto).getHttpResponse();
    }

    /**
     * Get all substitutes based on the teacher
     * @param dto filter for substitutes
     * @return found substitutes
     */
    @GetMapping("/substitute/teacher")
    public Object getSubstitutesByTeacher(@RequestBody @Valid GetSubstitutesByTeacherRequestDTO dto) {
        return this.substituteController.getSubstitutesByTeacher(dto).getHttpResponse();
    }

    /**
     * Get all substitutes based on the substitute teacher
     * @param dto filter for substitutes
     * @return found substitutes
     */
    @GetMapping("/substitute/substituteTeacher")
    public Object getSubstitutesBySubstituteTeacher(@RequestBody @Valid GetSubstitutesBySubstituteTeacherRequestDTO dto) {
        return this.substituteController.getSubstitutesBySubstituteTeacher(dto).getHttpResponse();
    }

}
