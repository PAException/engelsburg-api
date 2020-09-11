package io.github.paexception.engelsburginfrastructure.database.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class ReportAbsenceModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int reportAbsenceId;

    private int lesson;
    private String teacher; //TODO only needed if timetable isn't implemented
    private String student; //TODO maybe use foreign key/own model
    private String cause;

}
