package io.github.paexception.engelsburginfrastructure.controller;

import io.github.paexception.engelsburginfrastructure.database.repository.ReportAbsenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReportAbsenceController {

    @Autowired private ReportAbsenceRepository reportAbsenceRepository;

}
