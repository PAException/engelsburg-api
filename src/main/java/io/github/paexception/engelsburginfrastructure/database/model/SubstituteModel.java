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
public class SubstituteModel {

    //TODO check accuracy

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int substituteId;

    private String className;
    private int lesson;
    private String subject;
    private String substituteTeacher;
    private String teacher;
    private String type;
    //private String substituteFor;?
    private String room;
    private String text;

}
