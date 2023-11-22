/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.service.scheduled;

import io.github.paexception.engelsburg.api.controller.reserved.InformationController;
import io.github.paexception.engelsburg.api.controller.reserved.SubstituteController;
import io.github.paexception.engelsburg.api.controller.reserved.SubstituteMessageController;
import io.github.paexception.engelsburg.api.endpoint.dto.SubstituteDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.request.CreateSubstituteMessageRequestDTO;
import io.github.paexception.engelsburg.api.service.HtmlFetchingService;
import io.github.paexception.engelsburg.api.util.LoggingComponent;
import lombok.AllArgsConstructor;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

/**
 * Service to update substitutes.
 */
@Service
@AllArgsConstructor
public class SubstituteUpdateService extends HtmlFetchingService implements LoggingComponent {

	private static final Logger LOGGER = LoggerFactory.getLogger(SubstituteUpdateService.class);
	private final SubstituteController substituteController;
	private final SubstituteMessageController substituteMessageController;
	private final InformationController informationController;

	/**
	 * Scheduled function to update substitutes every 5 minutes.
	 */
	@Scheduled(fixedRate = 60 * 1000)
	public void updateSubstitutes() {
		if ("false".equals(System.getProperty("app.scheduling.enable"))) return;
		LOGGER.debug("[SUBSTITUTE] Fetching...");
		int count = 0;

		try {
			String navBarUrl = "https://engelsburg.smmp.de/vertretungsplaene/eng/Stp_Upload/frames/navbar.htm";
			Document navbar = this.request(navBarUrl);
			LOGGER.trace("[SUBSTITUTE] Requested navbar");

			Map<Integer, Integer> weeks = new HashMap<>(); //Week of year, Year
			//e.g.
			// <select name="week" class="selectbox" ...>
			//   <option value="37">11.9.2023</option>
			//   <option value="38">18.9.2023</option>
			// </select>
			String cssQuery = "select[name=week].selectBox > option";
			for (Element element : navbar.select(cssQuery)) {
				//e.g. <option value="37">11.9.2023</option>
				int weekOfYear = Integer.parseInt(element.attr("value")); //e.g. 37
				int year = Integer.parseInt(element.text().substring(element.text().lastIndexOf('.') + 1)); //e.g. 2023

				weeks.put(weekOfYear, year);
			}
			LOGGER.trace("[SUBSTITUTE] Parsed weeks: " + weeks.keySet());

			//e.g. var classes = ["5a","5b","5c",...,"Q4"];
			String startIdentifier = "var classes = [";
			String endIdentifier = "];";
			int startClasses = navbar.html().indexOf(startIdentifier);
			int endClasses = navbar.html().indexOf(endIdentifier);

			// --> school might have changed the declaration of the classes
			if (startClasses >= 0 && endClasses > startClasses) {
				//Extract classes and check for changes, if changed update to informationController
				String rawClasses = navbar.html().substring(startClasses, endClasses);
				if (this.checkChanges(rawClasses, "classes")) {
					rawClasses = rawClasses
							.replace(startIdentifier, "")
							.replace(endIdentifier, "")
							.replaceAll("\"", "");

					//Update current classes
					if (informationController != null) {
						this.informationController.setCurrentClasses(rawClasses.split(","));
						LOGGER.trace("[SUBSTITUTE] Updated classes");
					} else LOGGER.warn("[SUBSTITUTE] DRY RUN! Did not write classes to database");
				} else LOGGER.trace("[SUBSTITUTE] Classes did not change");
			}

			for (int week : weeks.keySet()) { //Iterate weeks
				String requestUrl = "https://engelsburg.smmp.de/vertretungsplaene/eng/Stp_Upload/" + week + "/w/w00000.htm";
				//e.g.
				// 1 <div id="vertretung">
				// 2   <a name="1">&nbsp;</a>
				// 3   <br>
				// 4   <b>11.9. Montag</b>
				// 5   <a href="#2">[ Dienstag ]</a>
				// 6   <a href="#3">[ Mittwoch ]</a>
				// 7   <a href="#4">[ Donnerstag ]</a>
				// 8   <a href="#5">[ Freitag ]</a>
				// 9   <p>
				//10     <table class="subst">...</table>
				//11   </p>
				//12   <p>
				//13     <a name="2">&nbsp;</a>
				//14     <br>
				//15     <a href="#1">[ Montag ]</a>
				//16     <b>12.9. Dienstag</b>
				//17     ...
				//18   </p>
				//19   <p>
				//20     <table ...>...</table>    <!-- substitute messages
				//21   </p>
				//22   <p>
				//23     <table class="subst">...</table>    <!-- actual substitutes
				//24   </p>
				//25   ...
				//26   <p> </p>
				//27</div>
				Element substitute = this.request(requestUrl).getElementById("vertretung");
				LOGGER.trace("[SUBSTITUTE] Requested substitutes of week " + week);

				//If substitutes have not changed continue with next week
				if (substitute == null || !this.checkChanges(substitute, "substitutes." + week)) {
					LOGGER.trace("[SUBSTITUTE] Substitutes of week " + week + " did not change");
					continue;
				}

				//Parse date to start with, --> [4]
				String rawDate = substitute.child(2).text();
				String dayAndMonth = rawDate.substring(0, rawDate.lastIndexOf('.'));
				Date currentDate = this.parseDate(dayAndMonth, weeks.get(week));
				LOGGER.trace("[SUBSTITUTE] First date of week is " + dayAndMonth + "." + weeks.get(week));

				//Remove the already used date elements, [2] - [8]
				List<Element> substituteContentToParse = substitute.children();
				for (Element paragraph : substituteContentToParse) {
					//if (!paragraph.tagName().equals("p") || paragraph.children().isEmpty()) continue;
					if ((!paragraph.tagName().equals("p") && !paragraph.tagName().equals("table")) || paragraph.children().isEmpty()) continue;

					//Element table = paragraph.child(0);
					Element table = paragraph; //Might be a bug

					//If the tagName is not equal to "table", there will be information about the current date, --> [16]
					if (table.tagName().equals("table")) {
						//If table has class "subst" then it will contain the actual substitutes
						//Otherwise it will contain the substitute messages
						// --> [20], --> [23]
						if (table.hasClass("subst")) {
							List<SubstituteDTO> substitutes = new ArrayList<>();

							// --> [23]
							// <table class="subst">
							//   <tbody>
							//     <tr class="list">...</tr>    <!-- Header
							//     <tr class="list odd">
							//       <td ...>
							//         <b>8e</b>
							//       </td>
							//       <td ...>1-13</td>
							//       <td ...>&nbsp;</td>
							//       <td ...>&nbsp;</td>
							//       <td ...>&nbsp;</td>
							//       <td ...>Veranst.</td>
							//       <td ...>&nbsp;</td>
							//       <td ...>&nbsp;</td>
							//       <td ...>
							//         <b>ALL</b>
							//       </td>
							//     </tr>
							//     <tr class="list even">...</tr>
							//   </tbody>
							// </table>
							int splitSubstitute = 0;
							for (Element row : table.child(0).children()) {
								//Skip header of row
								if (!row.hasClass("odd") && !row.hasClass("even")) continue;

								//If the row does not contain a className then this row is used to extend the text from
								// the previous substitute, so it needs to be added to the latest substitute
								String className = row.child(0).text();
								if (!substitutes.isEmpty() && !className.matches("(.*)[0-9](.*)")) {
									this.appendTextOnLastSubstitute(row, substitutes);
									String appendedText = substitutes.get(substitutes.size() - 1).getText();

									//There is also a possibility that the previous substitute will go over more than
									// one hour, in this case every single one needs to be updated
									for (int i = 1; i <= splitSubstitute; i++) {
										substitutes.get(substitutes.size() - 1 - i).setText(appendedText);
									}

									splitSubstitute = 0;
								} else {
									var newDtos = this.createSubstituteDTOs(row, currentDate);

									//If more than one substitute is returned it must have been split
									splitSubstitute = newDtos.size() - 1;
									substitutes.addAll(newDtos);
								}

							}

							//After cycling through the list update all substitutes to the controller
							if (!substitutes.isEmpty()) {
								if (this.substituteController != null) {
									this.substituteController.updateSubstitutes(substitutes, currentDate);
								} else LOGGER.warn("[SUBSTITUTE] DRY RUN! Did not write substitutes to database");

								LOGGER.trace("[SUBSTITUTE] Updated substitutes: " + substitutes.size());
								count += substitutes.size();
							} else LOGGER.trace("[SUBSTITUTE] No substitutes updated");
						} else {
							//Parse information to get the latest substitute message
							CreateSubstituteMessageRequestDTO dto = new CreateSubstituteMessageRequestDTO();
							dto.setDate(currentDate);

							Elements tableEntries = paragraph.getElementsByTag("td");
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

							//Update substitute message of current day to the controller
							if (substituteMessageController != null) {
								this.substituteMessageController.clearSubstituteMessages(currentDate);
								this.substituteMessageController.createSubstituteMessage(dto);
								LOGGER.trace("[SUBSTITUTE] Updated substitute message");
							} else LOGGER.warn("[SUBSTITUTE] DRY RUN! Did not write substitute messages to database");
						}
					} else {
						//Update the current date
						Elements days = paragraph.getElementsByTag("b");
						if (!days.isEmpty()) {
							rawDate = days.get(0).text();
							dayAndMonth = rawDate.substring(0, rawDate.lastIndexOf('.'));
							currentDate = this.parseDate(dayAndMonth, weeks.get(week));
							LOGGER.trace("[SUBSTITUTE] Switching to new date: " + dayAndMonth + "." + weeks.get(week));
						}
					}
				}

			}

			if (count > 0) LOGGER.info("[SUBSTITUTE] Fetched " + count);
			else LOGGER.debug("[SUBSTITUTE] Not changed");
		} catch (IOException | ParseException e) {
			this.logError("[SUBSTITUTE] Couldn't fetch", e, LOGGER);
		}
	}

	/**
	 * Function to create a substitute dto out of a html row.
	 *
	 * @param row         with substitute information
	 * @param currentDate current date to assign to substitute
	 * @return substitute dto
	 */
	private List<SubstituteDTO> createSubstituteDTOs(Element row, Date currentDate) {
		List<SubstituteDTO> dtos = new ArrayList<>();
		SubstituteDTO dto = new SubstituteDTO();
		dto.setDate(currentDate);
		dto.setClassName(row.child(0).text());
		String lessons = row.child(1).text().replace(" ", "");
		if (lessons.contains("-")) { //5 - 6, //3 - 6
			int low = Integer.parseInt(lessons.substring(0, lessons.indexOf("-"))),
					high = Integer.parseInt(lessons.substring(lessons.indexOf("-") + 1));

			for (int i = low; i < high; i++) {
				row.children().set(1, row.child(1).text(String.valueOf(i)));
				dtos.addAll(this.createSubstituteDTOs(row, currentDate));
			}

			dto.setLesson(high);
		} else dto.setLesson(Integer.parseInt(lessons));
		if (row.child(2).text().matches("(.*)[0-9](.*)")) dto.setSubject(row.child(2).text());
		if (row.child(3).text().matches("(.*)[a-zA-ZäöüÄÖÜ0-9](.*)")) dto.setSubstituteTeacher(row.child(3).text());
		if (row.child(4).text().matches("(.*)[a-zA-ZäöüÄÖÜ0-9](.*)")) dto.setTeacher(row.child(4).text());
		dto.setType(row.child(5).text());
		if (row.child(6).text().matches("(.*)[0-9](.*)")) dto.setSubstituteOf(row.child(6).text());
		if (!row.child(7).text().equals("---")) dto.setRoom(row.child(7).text());
		if (!row.child(8).text().matches("\\h")) dto.setText(row.child(8).text());

		dtos.add(dto);
		return dtos;
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

		for (; indexOfLastSubstitute >= 0; indexOfLastSubstitute--) {
			SubstituteDTO dto = substitutes.get(indexOfLastSubstitute);
			substitutes.set(indexOfLastSubstitute, dto.appendText(textToAppend));

			if (indexOfLastSubstitute > 0) {
				if (!dto.sameBase(substitutes.get(indexOfLastSubstitute - 1))) break;
			}
		}
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
