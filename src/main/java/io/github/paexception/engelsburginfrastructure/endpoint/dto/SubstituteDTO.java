package io.github.paexception.engelsburginfrastructure.endpoint.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotNull;
import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubstituteDTO {

    @NotNull
    private Date date;
    private String className;
    private String lesson;
    private String subject;
    private String substituteTeacher;
    private String teacher;
    private String type;
    private String substituteOf;
    private String room;
    private String text;

    public SubstituteDTO appendText(String text) {
        this.text = this.text + " " + text;

        return this;
    }

}
