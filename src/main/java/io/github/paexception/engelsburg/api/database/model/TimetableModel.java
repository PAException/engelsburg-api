/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.database.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

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

	@NotNull
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "semester_semesterId")
	private SemesterModel semester;
	@NotNull
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "subjectId")
	@ManyToOne
	@JoinColumn(name = "subject_subjectId")
	private SubjectModel subject;

	@Range(min = 0, max = 4)//MON to FRI
	private int day;
	@Range(min = 0, max = 10)//1 to 11 lesson
	private int lesson;
	private String teacher;
	private String className;
	private String room;

	public static TimetableModel template(SemesterModel semester, int day, int lesson) {
		TimetableModel timetable = new TimetableModel();
		timetable.setTimetableId(-1);
		timetable.setSemester(semester);
		timetable.setDay(day);
		timetable.setLesson(lesson);
		return timetable;
	}

	public TimetableDTO toResponseDTO() {
		return new TimetableDTO(
				this.day,
				this.lesson,
				this.subject.getSubjectId(),
				this.teacher,
				this.className,
				this.room
		);
	}
}
