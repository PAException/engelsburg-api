package io.github.paexception.engelsburginfrastructure.database.model;

import io.github.paexception.engelsburginfrastructure.endpoint.dto.response.SubstituteResponseDTO;
import io.github.paexception.engelsburginfrastructure.util.Validation;
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

    public SubstituteModel updateDate(Date date) {
        if (date != null) this.date = date;

        return this;
    }

    public SubstituteModel updateClassName(String className) {
        if (Validation.validateNotNullOrEmpty(className)) this.className = className;

        return this;
    }

    public SubstituteModel updateLesson(String lesson) {
        if (Validation.validateNotNullOrEmpty(lesson)) this.lesson = lesson;

        return this;
    }

    public SubstituteModel updateSubject(String subject) {
        if (Validation.validateNotNullOrEmpty(subject)) this.subject = subject;

        return this;
    }

    public SubstituteModel updateSubstituteTeacher(String substituteTeacher) {
        if (Validation.validateNotNullOrEmpty(substituteTeacher)) this.substituteTeacher = substituteTeacher;

        return this;
    }

    public SubstituteModel updateTeacher(String teacher) {
        if (Validation.validateNotNullOrEmpty(teacher)) this.teacher = teacher;

        return this;
    }

    public SubstituteModel updateType(String type) {
        if (Validation.validateNotNullOrEmpty(type)) this.type = type;

        return this;
    }

    public SubstituteModel updateSubstituteOf(String substituteOf) {
        if (Validation.validateNotNullOrEmpty(substituteOf)) this.substituteOf = substituteOf;

        return this;
    }

    public SubstituteModel updateRoom(String room) {
        if (Validation.validateNotNullOrEmpty(room)) this.room = room;

        return this;
    }

    public SubstituteModel updateText(String text) {
        if (Validation.validateNotNullOrEmpty(text)) this.text = text;

        return this;
    }

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
