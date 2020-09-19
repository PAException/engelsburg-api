package io.github.paexception.engelsburg.api.database.model;

import io.github.paexception.engelsburg.api.endpoint.dto.response.SubstituteMessageResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
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
    @Column(unique = true)
    private Date date;
    private String absenceTeachers;
    private String absenceClasses;
    private String affectedClasses;
    private String affectedRooms;
    private String blockedRooms;
    private String messages;

    public SubstituteMessageResponseDTO toResponseDTO() {
        return new SubstituteMessageResponseDTO(
                this.date,
                this.absenceTeachers,
                this.absenceClasses,
                this.affectedClasses,
                this.affectedRooms,
                this.blockedRooms,
                this.messages
        );
    }

}
