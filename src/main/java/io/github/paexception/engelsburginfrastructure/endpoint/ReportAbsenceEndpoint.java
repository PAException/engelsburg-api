package io.github.paexception.engelsburginfrastructure.endpoint;

import io.github.paexception.engelsburginfrastructure.controller.ReportAbsenceController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReportAbsenceEndpoint {

    @Autowired private ReportAbsenceController reportAbsenceController;

    //get reports of specific lesson by teacher scope and lesson

    //report absence for day (let controller do all entries for lessons) [maybe also ignore substitutes {just specific}] -> name of student, scope

    //report absence for specific lesson

    //get own reported absences

}
