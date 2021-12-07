package io.github.paexception.engelsburg.api.database.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.paexception.engelsburg.api.endpoint.dto.TimetableDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class TimetableModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int timetableId;

	@JsonIgnore
	@ManyToOne
	private UserModel user;
	@Range(min = 0, max = 4)//MON to FRI
	private int day;
	@Range(min = 0, max = 10)//1 to 11 lesson
	private int lesson;
	private String teacher;
	private String className;
	private String room;
	private String subject;

	public TimetableDTO toResponseDTO() {
		return new TimetableDTO(
				this.day,
				this.lesson,
				this.teacher,
				this.className,
				this.room,
				this.subject
		);
	}

}
