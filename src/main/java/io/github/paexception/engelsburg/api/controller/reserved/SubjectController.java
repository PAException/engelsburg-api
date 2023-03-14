/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.controller.reserved;

import io.github.paexception.engelsburg.api.database.model.SemesterModel;
import io.github.paexception.engelsburg.api.database.model.SubjectModel;
import io.github.paexception.engelsburg.api.database.repository.SubjectRepository;
import io.github.paexception.engelsburg.api.endpoint.dto.SubjectDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.UserDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.CreateSubjectRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.UpdateSubjectRequestDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.BaseSubjectsResponseDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.SubjectsResponseDTO;
import io.github.paexception.engelsburg.api.util.Error;
import io.github.paexception.engelsburg.api.util.Result;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import static io.github.paexception.engelsburg.api.util.Constants.Subject.NAME_KEY;

/**
 * Controller for subjects.
 */
@Component
@AllArgsConstructor
public class SubjectController {

	private final SubjectRepository subjectRepository;

	/**
	 * Create a new subject.
	 *
	 * @param dto      subject information
	 * @param semester bind to user
	 * @return Error or create subject
	 */
	public Result<SubjectDTO> createSubject(CreateSubjectRequestDTO dto, SemesterModel semester) {
		//Check if subject already exists, then return error
		if (this.subjectRepository.existsByBaseSubjectAndSemester(dto.getBaseSubject(), semester))
			return Result.of(Error.ALREADY_EXISTS, NAME_KEY);

		//Create and return newly created subject
		return Result.of(
				this.subjectRepository.save(
						BaseSubject.byName(dto.getBaseSubject()).toSubject(
								semester,
								dto.getCustomName(),
								dto.getColor(),
								dto.isAdvancedCourse()
						)
				).toResponseDTO()
		);
	}

	/**
	 * Copy subjects from semester to semester.
	 *
	 * @param old    semester to copy from
	 * @param copyTo semester to copy to
	 * @return a map with old and new subject
	 */
	public Map<SubjectModel, SubjectModel> copySubjects(SemesterModel old, SemesterModel copyTo) {
		List<SubjectModel> subjects = this.subjectRepository.findAllBySemester(old);
		Map<SubjectModel, SubjectModel> map = new HashMap<>();

		for (SubjectModel oldSubject : subjects) {
			SubjectModel subject = oldSubject.copy();
			subject.setSubjectId(-1);
			subject.setSemester(copyTo);
			SubjectModel newSubject = this.subjectRepository.saveAndFlush(subject);
			System.out.println("Test: old: " + subject.getSubjectId() + ", new: " + newSubject.getSubjectId());

			map.put(oldSubject, newSubject);
		}

		return map;
	}

	/**
	 * Update an existing subject.
	 *
	 * @param dto     with information to update
	 * @param userDTO user association
	 * @return Error or updated subject
	 */
	public Result<SubjectDTO> updateSubject(UpdateSubjectRequestDTO dto, UserDTO userDTO) {
		//Get optional subject, if not found return error
		Optional<SubjectModel> optionalSubject = this.subjectRepository.findById(dto.getSubjectId());
		if (optionalSubject.isEmpty()) return Result.of(Error.NOT_FOUND, NAME_KEY);

		//Check if user of subject is the requesting one or has superior permissions, if not return error
		SubjectModel subject = optionalSubject.get();
		if (!subject.getSemester().getUser().is(userDTO) && !userDTO.hasScope("subject.write.all"))
			return Result.of(Error.FORBIDDEN, NAME_KEY);

		//Update specific information
		subject.setAdvancedCourse(dto.isAdvancedCourse());
		if (dto.getCustomName() != null && !dto.getCustomName().isBlank()) subject.setCustomName(dto.getCustomName());
		if (dto.getColor() != null && !dto.getColor().isBlank()) subject.setColor(dto.getColor());

		//Save and return updated subject
		return Result.of(this.subjectRepository.save(subject).toResponseDTO());
	}

	/**
	 * Get a specific subject.
	 * <b>Internal use only</b>
	 *
	 * @param subjectId Subject identifier
	 * @param userDTO   User association
	 * @return Error or specific subject
	 */
	public Result<SubjectModel> getSubjectRaw(int subjectId, UserDTO userDTO) {
		//Get optional subject, if not found return error
		Optional<SubjectModel> optionalSubject = this.subjectRepository.findById(subjectId);
		if (optionalSubject.isEmpty()) return Result.of(Error.NOT_FOUND, NAME_KEY);

		//Check if user of subject is the requesting one or has superior permissions, if not return error
		SubjectModel subject = optionalSubject.get();
		if (!subject.getSemester().getUser().is(userDTO) && !userDTO.hasScope("subject.read.all"))
			return Result.of(Error.FORBIDDEN, NAME_KEY);

		//Return subject
		return Result.of(subject);
	}

	/**
	 * Get a specific subject.
	 *
	 * @param subjectId Subject identifier
	 * @param userDTO   User association
	 * @return Error or specific subject
	 */
	public Result<SubjectDTO> getSubject(int subjectId, UserDTO userDTO) {
		//Map subject to response dto
		return this.getSubjectRaw(subjectId, userDTO).map(SubjectModel::toResponseDTO);
	}

	/**
	 * Get all subjects of user.
	 *
	 * @param semester of subjects
	 * @return Error or all subjects
	 */
	public Result<List<SubjectModel>> getAllSubjectsRaw(SemesterModel semester) {
		//Get all subjects by user, if none found return error
		List<SubjectModel> subjects = this.subjectRepository.findAllBySemester(semester);
		if (subjects.isEmpty()) return Result.of(Error.NOT_FOUND, NAME_KEY);

		//Return subjects
		return Result.of(subjects);
	}

	/**
	 * Get all subjects of user.
	 *
	 * @param semester of subjects
	 * @return Error or all subjects
	 */
	public Result<SubjectsResponseDTO> getAllSubjects(SemesterModel semester) {
		//Map all subjects to response dtos
		return this.getAllSubjectsRaw(semester).map(subjects -> new SubjectsResponseDTO(subjects.stream()
				.map(SubjectModel::toResponseDTO)
				.collect(Collectors.toList())));
	}

	/**
	 * Delete a specific subject.
	 *
	 * @param subjectId Subject to delete
	 * @param userDTO   User association
	 * @return Empty result
	 */
	public Result<?> deleteSubject(int subjectId, UserDTO userDTO) {
		//Get optional subject, if not found return error
		Optional<SubjectModel> optionalSubject = this.subjectRepository.findById(subjectId);
		if (optionalSubject.isEmpty()) return Result.of(Error.NOT_FOUND, NAME_KEY);

		//Check if user of subject is the requesting one or has superior permissions, if not return error
		SubjectModel subject = optionalSubject.get();
		if (!subject.getSemester().getUser().is(userDTO) && !userDTO.hasScope("subject.delete.all"))
			return Result.of(Error.FORBIDDEN, NAME_KEY);

		//Fail if timetable or gradeShares are referencing to this subject
		if (subject.hasDepending()) return Result.of(Error.FAILED_DEPENDENCY, NAME_KEY);

		//Delete subject and return empty result
		this.subjectRepository.delete(subject);
		return Result.empty();
	}

	/**
	 * Static class to handle base subjects.
	 */
	@Getter
	public static class BaseSubject {

		public static final BaseSubject BIOLOGY = new BaseSubject("biology", Group.SCIENTIFIC);
		public static final BaseSubject CHEMISTRY = new BaseSubject("chemistry", Group.SCIENTIFIC);
		public static final BaseSubject INFORMATICS = new BaseSubject("informatics", Group.SCIENTIFIC);
		public static final BaseSubject MATH = new BaseSubject("math", Group.SCIENTIFIC);
		public static final BaseSubject PHYSICS = new BaseSubject("physics", Group.SCIENTIFIC);

		public static final BaseSubject ETHICS = new BaseSubject("ethics", Group.SOCIAL_SCIENTIFIC);
		public static final BaseSubject GEOGRAPHY = new BaseSubject("geography", Group.SOCIAL_SCIENTIFIC);
		public static final BaseSubject HISTORY = new BaseSubject("history", Group.SOCIAL_SCIENTIFIC);
		public static final BaseSubject PHILOSOPHY = new BaseSubject("philosophy", Group.SOCIAL_SCIENTIFIC);
		public static final BaseSubject PSYCHOLOGIE = new BaseSubject("psychology", Group.SOCIAL_SCIENTIFIC);
		public static final BaseSubject RELIGION = new BaseSubject("religion", Group.SOCIAL_SCIENTIFIC);
		public static final BaseSubject ECONOMICS = new BaseSubject("economics", Group.SOCIAL_SCIENTIFIC);

		public static final BaseSubject GERMAN = new BaseSubject("german", Group.LINGUISTICALLY_LITERARY);
		public static final BaseSubject ART = new BaseSubject("art", Group.LINGUISTICALLY_LITERARY);
		public static final BaseSubject MUSIC = new BaseSubject("music", Group.LINGUISTICALLY_LITERARY);

		public static final BaseSubject ENGLISH = new BaseSubject("english", Group.FOREIGN_LINGUISTICALLY);
		public static final BaseSubject FRENCH = new BaseSubject("french", Group.FOREIGN_LINGUISTICALLY);
		public static final BaseSubject GREEK = new BaseSubject("greek", Group.FOREIGN_LINGUISTICALLY);
		public static final BaseSubject ITALIAN = new BaseSubject("italian", Group.FOREIGN_LINGUISTICALLY);
		public static final BaseSubject LATIN = new BaseSubject("latin", Group.FOREIGN_LINGUISTICALLY);
		public static final BaseSubject SPANISH = new BaseSubject("spanish", Group.FOREIGN_LINGUISTICALLY);

		public static final BaseSubject SPORT = new BaseSubject("sport", Group.OTHER);

		@Getter
		private static final List<BaseSubject> ALL = List.of(BIOLOGY, CHEMISTRY, INFORMATICS, MATH, PHYSICS, ETHICS,
				GEOGRAPHY, HISTORY, PHILOSOPHY, PSYCHOLOGIE, RELIGION, ECONOMICS, GERMAN, ART, MUSIC, ENGLISH, FRENCH,
				GREEK, ITALIAN, LATIN, SPANISH, SPORT);

		@Schema(example = "math")
		private final String name;
		@Schema(example = "SCIENTIFIC")
		private final Group group;

		BaseSubject(String name, Group group) {
			this.name = name;
			this.group = group;
		}

		/**
		 * Get baseSubject by the name.
		 *
		 * @param name of baseSubject
		 * @return BaseSubject
		 */
		public static BaseSubject byName(String name) {
			return ALL.stream().filter(subject -> subject.getName().equalsIgnoreCase(name)).findAny().orElse(
					new BaseSubject(name, Group.OTHER));
		}

		/**
		 * Shortcut to parse baseSubject instance to a subjectModel.
		 *
		 * @param semester       To create subjectModel
		 * @param customName     To create subjectModel
		 * @param color          To create subjectModel
		 * @param advancedCourse To create subjectModel
		 * @return Newly created subjectModel
		 */
		public SubjectModel toSubject(SemesterModel semester, String customName, String color, boolean advancedCourse) {
			if (customName == null) customName = this.getName();

			return new SubjectModel(-1, semester, this.getName(), customName, color, advancedCourse);
		}

		enum Group {
			SCIENTIFIC,
			SOCIAL_SCIENTIFIC,
			LINGUISTICALLY_LITERARY,
			FOREIGN_LINGUISTICALLY,
			OTHER
		}
	}

	/**
	 * Get all base subjects.
	 *
	 * @return base subjects
	 */
	public Result<BaseSubjectsResponseDTO> getBaseSubjects() {
		return Result.of(new BaseSubjectsResponseDTO(BaseSubject.getALL()));
	}
}
