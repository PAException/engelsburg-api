/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.controller.reserved;

import io.github.paexception.engelsburg.api.database.model.TeacherModel;
import io.github.paexception.engelsburg.api.database.repository.TeacherRepository;
import io.github.paexception.engelsburg.api.endpoint.dto.TeacherDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.GetClassesResponseDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.GetTeachersResponseDTO;
import io.github.paexception.engelsburg.api.service.scheduled.SubstituteUpdateService;
import io.github.paexception.engelsburg.api.util.Error;
import io.github.paexception.engelsburg.api.util.LoggingComponent;
import io.github.paexception.engelsburg.api.util.Result;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.github.paexception.engelsburg.api.database.model.TeacherModel.Job.BIOLOGIE;
import static io.github.paexception.engelsburg.api.database.model.TeacherModel.Job.CHEMIE;
import static io.github.paexception.engelsburg.api.database.model.TeacherModel.Job.DEUTSCH;
import static io.github.paexception.engelsburg.api.database.model.TeacherModel.Job.ENGLISCH;
import static io.github.paexception.engelsburg.api.database.model.TeacherModel.Job.ERDKUNDE;
import static io.github.paexception.engelsburg.api.database.model.TeacherModel.Job.EV_RELIGION;
import static io.github.paexception.engelsburg.api.database.model.TeacherModel.Job.FRANZOESISCH;
import static io.github.paexception.engelsburg.api.database.model.TeacherModel.Job.GESCHICHTE;
import static io.github.paexception.engelsburg.api.database.model.TeacherModel.Job.INFORMATIK;
import static io.github.paexception.engelsburg.api.database.model.TeacherModel.Job.KATH_RELIGION;
import static io.github.paexception.engelsburg.api.database.model.TeacherModel.Job.KUNST;
import static io.github.paexception.engelsburg.api.database.model.TeacherModel.Job.LATEIN;
import static io.github.paexception.engelsburg.api.database.model.TeacherModel.Job.MATHE;
import static io.github.paexception.engelsburg.api.database.model.TeacherModel.Job.MUSIK;
import static io.github.paexception.engelsburg.api.database.model.TeacherModel.Job.PHILOSOPHIE;
import static io.github.paexception.engelsburg.api.database.model.TeacherModel.Job.PHYSIK;
import static io.github.paexception.engelsburg.api.database.model.TeacherModel.Job.POWI;
import static io.github.paexception.engelsburg.api.database.model.TeacherModel.Job.RUSSISCH;
import static io.github.paexception.engelsburg.api.database.model.TeacherModel.Job.SOFTWARESCHULUNG;
import static io.github.paexception.engelsburg.api.database.model.TeacherModel.Job.SPANISCH;
import static io.github.paexception.engelsburg.api.database.model.TeacherModel.Job.SPORT;
import static io.github.paexception.engelsburg.api.database.model.TeacherModel.Job.getJobId;
import static io.github.paexception.engelsburg.api.util.Constants.Information.NAME_KEY;

/**
 * Controller for all other information.
 */
@Component
@AllArgsConstructor
public class InformationController implements LoggingComponent {

	private static final Logger LOGGER = LoggerFactory.getLogger(InformationController.class);
	private static String[] currentClasses;
	private final TeacherRepository teacherRepository;

	/**
	 * Get a teacher by its abbreviation.
	 *
	 * @param abbreviation to identify teacher
	 * @return DTO with information about the teacher
	 */
	public Result<TeacherDTO> getTeacher(String abbreviation) {
		//Find teacher by abbreviation
		Optional<TeacherModel> optionalTeacher = this.teacherRepository.findByAbbreviation(abbreviation);

		//If found return as response dto otherwise return error
		return optionalTeacher.map(teacherModel -> Result.of(teacherModel.toResponseDTO()))
				.orElseGet(() -> Result.of(Error.NOT_FOUND, NAME_KEY));
	}

	/**
	 * Add teachers manually on startup.
	 * <a>https:engelsburg.smmp.de/unser-gymnasium/lehrer/</a>
	 */
	@Scheduled(fixedDelay = Long.MAX_VALUE, initialDelay = 30 * 1000)
	public void setTeacher() {
		LOGGER.debug("[Information] Starting to add teachers");
		//Delete all teacher
		this.teacherRepository.deleteAll();
		List<TeacherModel> teachers = new ArrayList<>();

		//Lehrer
		teachers.add(
				new TeacherModel(-1, "AHE", "Christine", "von der Ahé", "female", false, getJobId(BIOLOGIE, SPORT)));
		teachers.add(new TeacherModel(-1, "ALT", "Bärbel", "Althaus", "female", false, getJobId(MUSIK, DEUTSCH)));
		teachers.add(new TeacherModel(-1, "ANS", "Julia", "Anselmann", "female", false, getJobId(DEUTSCH, POWI)));
		teachers.add(new TeacherModel(-1, "APO", "Janine", "Apostel", "female", false, getJobId(MATHE, KATH_RELIGION)));
		teachers.add(new TeacherModel(-1, "ARE", "Heribert", "Arend", "male", false,
				getJobId(BIOLOGIE, CHEMIE, SOFTWARESCHULUNG)));
		teachers.add(
				new TeacherModel(-1, "BEC", "Holger", "Becker", "male", false, getJobId(PHYSIK, FRANZOESISCH, POWI)));
		teachers.add(new TeacherModel(-1, "BOR", "Marta", "Borg", "female", false, getJobId(MATHE, PHYSIK)));
		//teachers.add(new TeacherModel(-1, "BÖT?", "Johanna", "Böttner", "female", false, getJobId(MATHE, EV_RELIGION)));
		teachers.add(new TeacherModel(-1, "BRA", "Anja", "Bremer", "female", false,
				getJobId(MATHE, INFORMATIK, SOFTWARESCHULUNG)));
		teachers.add(
				new TeacherModel(-1, "BRM", "Michael", "Bremer", "male", false, getJobId(ENGLISCH, KATH_RELIGION)));
		teachers.add(
				new TeacherModel(-1, "BSU", "Jan", "Bültemeier-Sukovsky", "male", false, getJobId(SPORT, EV_RELIGION)));
		teachers.add(new TeacherModel(-1, "CAW", "Derek", "Cawley", "male", false, getJobId(ENGLISCH, ERDKUNDE)));
		//Probably just one of religion but it's not defined, please remove wrong
		teachers.add(new TeacherModel(-1, "DIL", "Sabine", "Diller", "female", false,
				getJobId(BIOLOGIE, KATH_RELIGION, EV_RELIGION)));
		teachers.add(new TeacherModel(-1, "DIT", "Boris", "Dittmeier", "male", false, getJobId(DEUTSCH, POWI)));
		teachers.add(new TeacherModel(-1, "DÖR", "Lisa", "Döring", "female", false, getJobId(BIOLOGIE, CHEMIE)));
		teachers.add(new TeacherModel(-1, "EIC", "Henning", "Eickmeyer", "male", false, getJobId(ENGLISCH, SPORT)));
		teachers.add(new TeacherModel(-1, "EUL", "Stephan-Alexander", "Eull", "male", false, getJobId(POWI, ERDKUNDE)));
		teachers.add(
				new TeacherModel(-1, "GAR", "Esmerlindo C.", "Garcia Mateos", "male", false, getJobId(MATHE, CHEMIE)));
		teachers.add(
				new TeacherModel(-1, "GIJ", "Judith", "Giede-Jeppe", "female", false, getJobId(ENGLISCH, BIOLOGIE)));
		teachers.add(new TeacherModel(-1, "GLE", "Jens", "Glebe", "male", false, getJobId(ENGLISCH, EV_RELIGION)));
		//Outdated but not updated on so surname and abbreviation are unknown
		teachers.add(new TeacherModel(-1, "GOE", "Katharina", "Goeb", "female", false, getJobId(SPORT, KATH_RELIGION)));
		teachers.add(new TeacherModel(-1, "GRB", "Sylvia", "Grabarczyk", "female", false, getJobId(DEUTSCH, BIOLOGIE)));
		teachers.add(new TeacherModel(-1, "GRF", "Corinna", "Graf", "female", false, getJobId(DEUTSCH, EV_RELIGION)));
		teachers.add(new TeacherModel(-1, "HAN", "Volker", "Hahn-Dwier", "male", false, getJobId(KUNST, POWI)));
		teachers.add(new TeacherModel(-1, "HEC", "Melanie", "Heczko", "female", false,
				getJobId(DEUTSCH, MUSIK, KATH_RELIGION)));
		teachers.add(new TeacherModel(-1, "HEG", "Isabell", "Hegner", "female", false,
				getJobId(DEUTSCH, MATHE, KATH_RELIGION)));
		teachers.add(new TeacherModel(-1, "HEI", "Oliver", "Heinemann", "male", false, getJobId(MATHE, POWI)));
		//teachers.add(new TeacherModel(-1, "HER?", "Kamila Karolina", "Hertel", "female", false, getJobId(ENGLISCH, EV_RELIGION)));
		teachers.add(new TeacherModel(-1, "HIT", "Sandra", "Hitzel", "female", true, getJobId(MATHE, CHEMIE)));
		teachers.add(
				new TeacherModel(-1, "HOL", "Jessica", "Holst", "female", false, getJobId(ENGLISCH, FRANZOESISCH)));
		//teachers.add(new TeacherModel(-1, "HOR?", "Laura", "Hornung", "female", false, getJobId(LATEIN, KUNST)));
		teachers.add(new TeacherModel(-1, "IHM", "Dierk", "Ihmor", "male", false, getJobId(MATHE, PHYSIK)));
		teachers.add(new TeacherModel(-1, "JÜN", "Eileen", "Jünemann", "female", false, getJobId(ENGLISCH, LATEIN)));
		teachers.add(new TeacherModel(-1, "JUN", "Markus", "Junghans", "male", false, getJobId(GESCHICHTE, POWI)));
		teachers.add(new TeacherModel(-1, "JUP", "Mario", "Jupé", "male", false, getJobId(LATEIN, GESCHICHTE)));
		teachers.add(new TeacherModel(-1, "KLE", "Georg", "Klein", "male", false, getJobId(MATHE, PHYSIK)));
		teachers.add(new TeacherModel(-1, "KLS", "Ann Kathrin", "Kleinschmit", "female", false,
				getJobId(ENGLISCH, FRANZOESISCH)));
		teachers.add(new TeacherModel(-1, "KLEP", "Kai", "Klepp", "male", false, getJobId(ENGLISCH, GESCHICHTE)));
		teachers.add(new TeacherModel(-1, "KNA", "Johannes", "Knauf", "male", true, getJobId(BIOLOGIE, SPORT)));
		teachers.add(new TeacherModel(-1, "KPA", "Karlheinz", "Kopanski", "male", true, getJobId(DEUTSCH, KUNST)));
		teachers.add(new TeacherModel(-1, "KOP", "Christine", "Koplin", "female", false, getJobId(ENGLISCH, DEUTSCH)));
		teachers.add(
				new TeacherModel(-1, "KRÄ", "Ulrike", "Krämer", "female", true, getJobId(FRANZOESISCH, GESCHICHTE)));
		teachers.add(
				new TeacherModel(-1, "KRH", "Heidy", "Krauledat-Haag", "female", false, getJobId(EV_RELIGION, LATEIN)));
		teachers.add(
				new TeacherModel(-1, "KRZ", "Annegret", "Kruse-Zimprich", "female", false, getJobId(LATEIN, RUSSISCH)));
		teachers.add(new TeacherModel(-1, "KÜH", "Hendrik", "Kühne", "male", false, getJobId(BIOLOGIE, CHEMIE)));
		teachers.add(
				new TeacherModel(-1, "LAN", "Michael", "Langer", "male", false, getJobId(PHYSIK, MATHE, INFORMATIK)));
		teachers.add(new TeacherModel(-1, "LEC", "Alexander", "Lecke", "male", true, getJobId(MATHE, PHYSIK)));
		teachers.add(
				new TeacherModel(-1, "LEI", "Otmar", "Leibold", "male", false, getJobId(KATH_RELIGION, PHILOSOPHIE)));
		teachers.add(new TeacherModel(-1, "LPP", "Helene", "Leippi", "female", false, getJobId(SOFTWARESCHULUNG)));
		teachers.add(new TeacherModel(-1, "LIN", "Katharina", "Lind", "female", false, getJobId(DEUTSCH, EV_RELIGION)));
		teachers.add(new TeacherModel(-1, "LOH", "Marie-Claire", "Lohéac-Wieders", "female", false,
				getJobId(FRANZOESISCH, DEUTSCH)));
		teachers.add(new TeacherModel(-1, "LHF", "Johanna", "Lohof", "female", false, getJobId(MATHE, DEUTSCH)));
		teachers.add(new TeacherModel(-1, "MES", "Michael", "Messner", "male", false, getJobId(PHYSIK, POWI, MATHE)));
		teachers.add(
				new TeacherModel(-1, "MÖM", "Ulrike", "Möller-Merz", "female", false, getJobId(ENGLISCH, ERDKUNDE)));
		teachers.add(new TeacherModel(-1, "MOE", "Mirko", "Moeller", "male", false, getJobId(ENGLISCH, MUSIK)));
		teachers.add(new TeacherModel(-1, "MOM", "Melanie", "Momberg", "female", false, getJobId(LATEIN, SPORT)));
		teachers.add(new TeacherModel(-1, "MÜM", "Maike", "Müller", "female", false, getJobId(MATHE, MUSIK)));
		teachers.add(
				new TeacherModel(-1, "MLL", "Winfried", "Müller", "male", false, getJobId(FRANZOESISCH, GESCHICHTE)));
		teachers.add(new TeacherModel(-1, "MUS", "Cheryl", "Musmann", "female", false, getJobId(ENGLISCH, POWI)));
		//teachers.add(new TeacherModel(-1, "NIS?", "Annette", "Nießner", "female", false, getJobId(BIOLOGIE, CHEMIE)));
		teachers.add(new TeacherModel(-1, "NIP", "Judith", "Nipper", "female", false,
				getJobId(KATH_RELIGION, KUNST, MATHE)));
		teachers.add(new TeacherModel(-1, "NOW", "Daniel", "Nowotny", "male", false, getJobId(SPORT, POWI)));
		teachers.add(new TeacherModel(-1, "OHM", "Birgit", "Ohmes-Hapke", "female", false, getJobId(DEUTSCH, POWI)));
		teachers.add(new TeacherModel(-1, "PÄH", "Dietlinde", "Pähler", "female", false, getJobId(ENGLISCH, SPANISCH)));
		teachers.add(new TeacherModel(-1, "PIG", "Marc", "Pigan", "male", false, getJobId(ERDKUNDE, SPORT)));
		teachers.add(new TeacherModel(-1, "PRA", "Kathrin", "Pramann", "female", false, getJobId(ERDKUNDE, BIOLOGIE)));
		teachers.add(new TeacherModel(-1, "PRI", "Thorsten", "Prinz", "male", false, getJobId(MATHE, KATH_RELIGION)));
		teachers.add(new TeacherModel(-1, "RAC", "Monika", "Rack", "female", true,
				getJobId(ENGLISCH, KATH_RELIGION, MATHE)));
		//teachers.add(new TeacherModel(-1, "REI", "Angela", "Reiss", "female", false, getJobId(DEUTSCH, PHILOSOPHIE)));
		teachers.add(new TeacherModel(-1, "ROS", "Detlef", "Rosenbach", "male", false, getJobId(DEUTSCH, GESCHICHTE)));
		teachers.add(new TeacherModel(-1, "RÜD", "Dominik", "Rüttjes", "male", false,
				getJobId(GESCHICHTE, MUSIK, ERDKUNDE)));
		teachers.add(
				new TeacherModel(-1, "RÜS", "Saskia", "Rüttjes", "female", false, getJobId(FRANZOESISCH, GESCHICHTE)));
		teachers.add(
				new TeacherModel(-1, "SAG", "Friederike", "Sagebiel", "female", false, getJobId(DEUTSCH, GESCHICHTE)));
		teachers.add(new TeacherModel(-1, "SCHF", "Arne", "Schäfer", "male", true, getJobId(PHYSIK, MATHE)));
		//teachers.add(new TeacherModel(-1, "SCH", "Heiner", "Schäfer", "male", true, getJobId(PHYSIK, BIOLOGIE)));
		teachers.add(new TeacherModel(-1, "SCHM", "Sebastian", "Schmitz", "male", false, getJobId(BIOLOGIE, DEUTSCH)));
		//teachers.add(new TeacherModel(-1, "SEH", "Christiane", "Seeling-Heinemann", "female", false, getJobId(EV_RELIGION, DEUTSCH)));
		//teachers.add(new TeacherModel(-1, "???", "Elisabeth", "Seidel", "female", false, getJobId(DEUTSCH, KUNST)));
		teachers.add(new TeacherModel(-1, "SEI", "Stefanie", "Seim", "female", true, getJobId(DEUTSCH, EV_RELIGION)));
		teachers.add(
				new TeacherModel(-1, "SÖL", "Cornelia", "Söllner", "female", false, getJobId(FRANZOESISCH, MATHE)));
		//teachers.add(new TeacherModel(-1, "SPE?", "Ralf", "Speckmann", "male", false, getJobId(DEUTSCH, SPORT)));
		teachers.add(
				new TeacherModel(-1, "STN", "Anja", "Stöcker-Nolzen", "female", false, getJobId(SPANISCH, DEUTSCH)));
		teachers.add(
				new TeacherModel(-1, "STÖ", "Eugen", "Stöckmann", "male", false, getJobId(KATH_RELIGION, GESCHICHTE)));
		//teachers.add(new TeacherModel(-1, "STY?", "Dorothee", "Stylianou", "female", true, getJobId(MATHE, POWI)));
		//teachers.add(new TeacherModel(-1, "VIE?", "Michaela", "Viereckt", "female", false, getJobId(TASTENSCHREIBEN, OFFICE)));
		teachers.add(new TeacherModel(-1, "VOGL", "Kathrin", "Vogler", "female", false, getJobId(MUSIK, ENGLISCH)));
		teachers.add(new TeacherModel(-1, "WEG", "Anselm", "Wegener", "male", false, getJobId(PHYSIK, MUSIK, MATHE)));
		teachers.add(new TeacherModel(-1, "WES", "Katharina", "Westerkamp", "female", false,
				getJobId(KUNST, KATH_RELIGION)));
		teachers.add(
				new TeacherModel(-1, "WIT", "Stephanie", "Wittmeier", "female", false, getJobId(DEUTSCH, ENGLISCH)));

		//School social workers
		//teachers.add(new TeacherModel(-1, "KÖH?", "Saskia", "Köhler", "female", false, getJobId(SCHULSOZIALARBEIT)));
		//teachers.add(new TeacherModel(-1, "HAL?", "Jan", "Halm", "male", false, getJobId(SCHULSOZIALARBEIT)));
		//teachers.add(new TeacherModel(-1, "BTL?", "Alisa", "Bechtel", "female", false, getJobId(SCHULSOZIALARBEIT)));
		//teachers.add(new TeacherModel(-1, "SAD?", "Vanessa", "Sadura", "female", false, getJobId(RELIGIONSPAEDAGOGISCHES_PRAKTIKUM_ALS_GEMEINDEASSISTENTIN)));

		//Teachers in the preparatory service
		teachers.add(new TeacherModel(-1, "BRI", "Christian", "Brinkmann", "male", false, getJobId(SPORT, POWI)));
		teachers.add(new TeacherModel(-1, "ELL", "Tillmann", "Eller", "male", false, getJobId(MUSIK, CHEMIE)));
		teachers.add(
				new TeacherModel(-1, "GLA", "Theresa", "Glaser", "female", false, getJobId(DEUTSCH, KATH_RELIGION)));
		//teachers.add(new TeacherModel(-1, "KRA", "Marcel", "Kramer", "male", false, getJobId(DEUTSCH, KATH_RELIGION)));
		//teachers.add(new TeacherModel(-1, "???", "Marian", "Müller", "female", false, getJobId(ENGLISCH, GESCHICHTE)));
		//teachers.add(new TeacherModel(-1, "NUS", "Alina", "Nußbaum", "female", false, getJobId(SPANISCH, ERDKUNDE)));
		//teachers.add(new TeacherModel(-1, "REG", "Katharina", "Regett", "female", false, getJobId(BIOLOGIE, GESCHICHTE, DEUTSCH)));
		//teachers.add(new TeacherModel(-1, "SEC", "Susanne", "Sechrist", "female", false, getJobId(ENGLISCH, MUSIK)));
		//teachers.add(new TeacherModel(-1, "WEI", "Marie", "Weidemann", "female", false, getJobId(BIOLOGIE, DEUTSCH)));
		teachers.add(
				new TeacherModel(-1, "WET", "Laura", "Wetzel", "female", false, getJobId(GESCHICHTE, FRANZOESISCH)));

		//Some teachers aren't listed on the website

		//Add all teachers
		this.teacherRepository.saveAll(teachers);
		LOGGER.info("[Information] Added " + teachers.size() + " teachers");
	}

	/**
	 * Get all current classes.
	 *
	 * @return current classes
	 */
	public Result<GetClassesResponseDTO> getCurrentClasses() {
		if (currentClasses.length == 0) return Result.of(Error.NOT_FOUND, NAME_KEY);

		return Result.of(new GetClassesResponseDTO(currentClasses));
	}

	/**
	 * Set current classes.
	 * Only {@link SubstituteUpdateService} is supposed to call
	 * this function!
	 *
	 * @param classes The current classes
	 */
	public void setCurrentClasses(String[] classes) {
		currentClasses = classes;
	}

	/**
	 * Get all registered teachers.
	 *
	 * @return DTO with all registered teachers
	 */
	public Result<GetTeachersResponseDTO> getAllTeachers() {
		//Get all teachers
		List<TeacherDTO> teacherDTOs = this.teacherRepository.findAll().stream()
				.map(TeacherModel::toResponseDTO).collect(Collectors.toList());

		//Return teachers, if empty error
		if (teacherDTOs.isEmpty()) return Result.of(Error.NOT_FOUND, NAME_KEY);
		else return Result.of(new GetTeachersResponseDTO(teacherDTOs));
	}
}
