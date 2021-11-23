package io.github.paexception.engelsburg.api.database.model;

import io.github.paexception.engelsburg.api.endpoint.dto.SubstituteMessageDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.sql.Date;

@Getter
@Setter
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

	public SubstituteMessageDTO toResponseDTO() {
		return new SubstituteMessageDTO(
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
