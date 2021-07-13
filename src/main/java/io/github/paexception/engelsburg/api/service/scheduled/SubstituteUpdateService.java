package io.github.paexception.engelsburg.api.service.scheduled;

import io.github.paexception.engelsburg.api.controller.reserved.InformationController;
import io.github.paexception.engelsburg.api.controller.reserved.SubstituteController;
import io.github.paexception.engelsburg.api.controller.reserved.SubstituteMessageController;
import io.github.paexception.engelsburg.api.endpoint.dto.SubstituteDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.CreateSubstituteMessageRequestDTO;
import io.github.paexception.engelsburg.api.util.LoggingComponent;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service to update substitutes.
 */
@Service
public class SubstituteUpdateService extends LoggingComponent {

	@Autowired
	private SubstituteController substituteController;
	@Autowired
	private SubstituteMessageController substituteMessageController;
	@Autowired
	private InformationController informationController;
	private Date currentDate;
	private List<SubstituteDTO> substitutes;

	public SubstituteUpdateService() {
		super(SubstituteUpdateService.class);
	}

	/**
	 * Scheduled function to update substitutes every 5 minutes.
	 */
	@Scheduled(fixedRate = 5 * 60 * 1000)
	public void updateSubstitutes() {
		this.logger.debug("Starting to fetch substitutes");
		try {
			Document navbar = Jsoup.connect("https://engelsburg.smmp.de/vertretungsplaene/ebg/Stp_Upload/frames/navbar.htm").get();

			Map<String, Integer> weeks = new HashMap<>(); //Week year
			navbar.getElementsByAttributeValue("name", "week") //Get weeks necessary to get substitutes
					.stream().filter(element -> element.hasClass("selectbox"))
					.collect(Collectors.toList()).forEach(element -> element.children()
					.forEach(element2 -> weeks.put(
							element2.attr("value"),
							Integer.parseInt(element2.text().substring(element2.text().lastIndexOf('.') + 1)))));

			this.informationController.setCurrentClasses(//Update current classes
					navbar.html().substring(navbar.html().indexOf("var classes = ["), navbar.html().indexOf("];"))
							.trim()
							.replace("var classes = [", "")
							.replace("];", "")
							.replaceAll("\"", "")
							.split(",")
			);

			for (String week : weeks.keySet()) { //Iterate weeks
				Element substitute = Jsoup.connect("https://engelsburg.smmp.de/vertretungsplaene/ebg/Stp_Upload/" + week + "/w/w00000.htm").get().getElementById("vertretung");
				if (substitute != null) {
					this.currentDate = this.parseDate(substitute.child(2).text().substring(0, substitute.child(2).text().lastIndexOf('.')), weeks.get(week));

					for (Element substituteContent : substitute.getAllElements().subList(8, substitute.getAllElements().size())) {
						if (substituteContent.tagName().equals("table")) {
							if (substituteContent.hasClass("subst")) {
								this.substitutes = new ArrayList<>();

								for (Element row : substituteContent.child(0).children())
									if (row.hasClass("odd") || row.hasClass("even"))
										if (this.substitutes.size() > 0 && !row.child(0).text().matches("(.*)[0-9](.*)"))
											this.appendTextOnLastSubstitute(row, this.substitutes);
										else this.substitutes.add(this.createSubstituteDTO(row));

								this.substituteController.updateSubstitutes(this.substitutes, this.currentDate);
							} else {
								CreateSubstituteMessageRequestDTO dto = new CreateSubstituteMessageRequestDTO();
								dto.setDate(this.currentDate);

								Elements tableEntries = substituteContent.getElementsByTag("td");
								for (int i = 0; i < tableEntries.size(); i += 2) {
									Element entry = tableEntries.get(i);
									if (entry.text().startsWith("Abwesende Lehrer"))
										dto.setAbsenceTeachers(tableEntries.get(i + 1).text());
									else if (entry.text().startsWith("Blockierte Räume"))
										dto.setBlockedRooms(tableEntries.get(i + 1).text());
									else if (entry.text().startsWith("Betroffene Klassen"))
										dto.setAffectedClasses(tableEntries.get(i + 1).text());
									else if (entry.text().startsWith("Betroffene Räume"))
										dto.setAffectedRooms(tableEntries.get(i + 1).text());
									else if (entry.text().startsWith("Abwesende Klassen"))
										dto.setAbsenceClasses(tableEntries.get(i + 1).text());
									else dto.setMessages(entry.text());
								}

								this.substituteMessageController.clearSubstituteMessages(this.currentDate);
								this.substituteMessageController.createSubstituteMessage(dto);
							}
						} else if (substituteContent.tagName().equals("p")) {
							Elements days = substituteContent.getElementsByTag("b");
							if (days.size() > 0) {
								String dayAndMonth = days.get(0).text().substring(0, days.get(0).text().lastIndexOf('.'));
								this.currentDate = this.parseDate(dayAndMonth, weeks.get(week));
							}
						}
					}
				}
			}
			this.logger.info("Updated substitutes");
		} catch (IOException | ParseException e) {
			this.logError("Couldn't fetch substitutes", e);
		}
	}

	/**
	 * Function to create a substitute dto out of an html row.
	 *
	 * @param row with substitute information
	 * @return substitute dto
	 */
	private SubstituteDTO createSubstituteDTO(Element row) {
		SubstituteDTO dto = new SubstituteDTO();
		dto.setDate(this.currentDate);
		dto.setClassName(row.child(0).text());
		if (row.child(1).text().contains("-")) {
			dto.setLesson(Integer.parseInt(row.child(1).text().replace(" ", "")
					.substring(0, row.child(1).text().replace(" ", "").indexOf("-"))));
			row.children().set(1,
					row.child(1).text(
							row.child(1).text().substring(
									row.child(1).text().indexOf("-") + 1)));
			this.substitutes.add(this.createSubstituteDTO(row));
		} else dto.setLesson(Integer.parseInt(row.child(1).text()));
		if (row.child(2).text().matches("(.*)[0-9](.*)")) dto.setSubject(row.child(2).text());
		dto.setSubstituteTeacher(row.child(3).text());
		dto.setTeacher(row.child(4).text());
		dto.setType(row.child(5).text());
		if (row.child(6).text().matches("(.*)[0-9](.*)")) dto.setSubstituteOf(row.child(6).text());
		if (!row.child(7).text().equals("---")) dto.setRoom(row.child(7).text());
		if (!row.child(8).text().matches("\\h")) dto.setText(row.child(8).text());

		return dto;
	}

	/**
	 * Private function to call if a row has no information except the text in the end which is used, to extend the writable.
	 * text of substitutes
	 *
	 * @param row         which is empty except the text in the end
	 * @param substitutes List of dtos to get the last one and append the text in the given row
	 */
	private void appendTextOnLastSubstitute(Element row, List<SubstituteDTO> substitutes) {
		int indexOfLastSubstitute = substitutes.size() - 1;
		String textToAppend = row.children().get(row.children().size() - 1).text();
		SubstituteDTO dto = substitutes.get(indexOfLastSubstitute);

		substitutes.set(indexOfLastSubstitute, dto.appendText(textToAppend));
	}

	/**
	 * Private function to parse a String with day and month and a year into a {@link Date}.
	 *
	 * @param dayAndMonth to parse
	 * @param year        to parse
	 * @return parsed Date
	 * @throws ParseException if something goes wrong while parsing the date
	 */
	private Date parseDate(String dayAndMonth, int year) throws ParseException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");

		return new Date(simpleDateFormat.parse(dayAndMonth + "." + year).getTime());
	}

}
