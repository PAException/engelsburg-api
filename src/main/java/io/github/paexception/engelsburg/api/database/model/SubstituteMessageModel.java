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
import javax.persistence.Lob;
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
	@Lob
	private String absenceTeachers;
	@Lob
	private String absenceClasses;
	@Lob
	private String affectedClasses;
	@Lob
	private String affectedRooms;
	@Lob
	private String blockedRooms;
	@Lob
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
