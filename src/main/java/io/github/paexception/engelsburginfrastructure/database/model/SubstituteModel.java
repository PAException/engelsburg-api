package io.github.paexception.engelsburginfrastructure.database.model;

import io.github.paexception.engelsburginfrastructure.endpoint.dto.response.SubstituteResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class SubstituteModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int substituteId;

    @NotNull
    private Date date;
    @NotBlank
    private String className;
    @NotBlank
    private String lesson;
    private String subject;
    @NotBlank
    private String substituteTeacher;
    @NotBlank
    private String teacher;
    @NotBlank
    private String type;
    private String substituteOf;
    private String room;
    private String text;

    public SubstituteResponseDTO toResponseDTO() {
        return new SubstituteResponseDTO(
                this.date,
                this.className,
                this.lesson,
                this.subject,
                this.substituteTeacher,
                this.teacher,
                this.type,
                this.substituteOf,
                this.room,
                this.text
        );
    }

}
