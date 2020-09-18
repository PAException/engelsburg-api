package io.github.paexception.engelsburginfrastructure.endpoint;

import io.github.paexception.engelsburginfrastructure.controller.SubstituteController;
import io.github.paexception.engelsburginfrastructure.endpoint.dto.request.GetSubstitutesByClassNameRequestDTO;
import io.github.paexception.engelsburginfrastructure.endpoint.dto.request.GetSubstitutesBySubstituteTeacherRequestDTO;
import io.github.paexception.engelsburginfrastructure.endpoint.dto.request.GetSubstitutesByTeacherRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;

@RestController
public class SubstituteEndpoint {

    @Autowired private SubstituteController substituteController;

    @GetMapping("/substitute")
    public Object getAllSubstitutes(@RequestParam(required = false, defaultValue = "0") long date) {
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
