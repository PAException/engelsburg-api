package io.github.paexception.engelsburginfrastructure.database.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class SubstituteMessageModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int substituteMessageId;

    @NotNull
    private Date date;
    private String absenceTeachers;
    private String affectedClasses;
    private String affectedRooms;
    private String messages;

}
