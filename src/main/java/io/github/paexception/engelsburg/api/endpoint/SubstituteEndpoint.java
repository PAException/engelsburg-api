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

@ServiceToken
@Validated
@RestController
public class SubstituteEndpoint {

    @Autowired private SubstituteController substituteController;

    /**
     * Get substitutes of a specific date
     * @param date to get substitutes
     * @return all substitutes of the specific date
     */
    @GetMapping("/substitute")
    public Object getAllSubstitutes(@RequestParam(required = false, defaultValue = "0") @Min(0) long date) {
        return this.substituteController.getAllSubstitutes(date).getHttpResponse();
    }

    /**
     * <b>Just returns all substitutes of the day and future</b>
     *
     * @param dto information and filters to get substitutes
     * @return adapted substitutes
     */
    @GetMapping("/substitute/className")
    public Object getSubstitutesByClassName(@RequestBody @Valid GetSubstitutesByClassNameRequestDTO dto) {
        return this.substituteController.getSubstitutesByClassName(dto).getHttpResponse();
    }

    @GetMapping("/substitute/teacher")
    public Object getSubstitutesByTeacher(@RequestBody @Valid GetSubstitutesByTeacherRequestDTO dto) {
        return this.substituteController.getSubstitutesByTeacher(dto).getHttpResponse();
    }

    @GetMapping("/substitute/substituteTeacher")
    public Object getSubstitutesBySubstituteTeacher(@RequestBody @Valid GetSubstitutesBySubstituteTeacherRequestDTO dto) {
        return this.substituteController.getSubstitutesBySubstituteTeacher(dto).getHttpResponse();
    }

}
