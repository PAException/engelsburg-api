package io.github.paexception.engelsburginfrastructure.service;

import io.github.paexception.engelsburginfrastructure.controller.SubstituteController;
import io.github.paexception.engelsburginfrastructure.endpoint.dto.SubstituteDTO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class SubstituteUpdateService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubstituteUpdateService.class.getSimpleName());
    @Autowired private SubstituteController substituteController;

    @Scheduled(fixedRate = 5000000)
    public void updateSubstitutes() {
        try {
            Document navbar = Jsoup.connect("https://engelsburg.smmp.de/vertretungsplaene/ebg/Stp_Upload/frames/navbar.htm").get();

            Map<Integer, Integer> weeks = new HashMap<>();
            navbar.getElementsByAttributeValue("name", "week")
                    .stream().filter(element -> element.hasClass("selectbox"))
                    .collect(Collectors.toList()).forEach(element -> element.children()
                    .forEach(element2 -> weeks.put(
                            Integer.parseInt(element2.attr("value")),
                            Integer.parseInt(element2.text().substring(element2.text().lastIndexOf('.')+1)))));

            for (int no : weeks.keySet()) {
                Element substitution = Jsoup.connect("https://engelsburg.smmp.de/vertretungsplaene/ebg/Stp_Upload/" + no + "/w/w00000.htm").get().getElementById("vertretung");
                Date currentDate = this.parseDate(substitution.child(2).text().substring(0, substitution.child(2).text().lastIndexOf('.')), weeks.get(no));
                for (Element element : substitution.getAllElements().subList(8, substitution.getAllElements().size())) {
                    if (element.tagName().equals("table")) {
                        if (element.hasClass("subst")) {
                            List<SubstituteDTO> substitutes = new ArrayList<>();
                            for (Element row : element.child(0).children()) {
                                if (row.hasClass("odd") || row.hasClass("even")) {
                                    if (substitutes.size()>0 && !row.child(0).text().matches("(.*)[0-9](.*)")) {
                                        SubstituteDTO dto = substitutes.get(substitutes.size()-1);
                                        substitutes.set(substitutes.size()-1, dto.appendText(row.children().get(row.children().size()-1).text()));
                                    } else {
                                        SubstituteDTO dto = new SubstituteDTO();
                                        dto.setDate(currentDate);
                                        dto.setClassName(row.child(0).text());
                                        dto.setLesson(row.child(1).text());
                                        if (!row.child(2).text().isBlank()) dto.setSubject(row.child(2).text());
                                        dto.setSubstituteTeacher(row.child(3).text());
                                        dto.setTeacher(row.child(4).text());
                                        dto.setType(row.child(5).text());
                                        if (!row.child(6).text().isBlank()) dto.setSubstituteOf(row.child(6).text());
                                        dto.setRoom(row.child(7).text());
                                        if (!row.child(8).text().isBlank()) dto.setText(row.child(8).text());
                                        substitutes.add(dto);
                                    }
                                }
                            }
                            substitutes.forEach(dto -> substituteController.createOrUpdateSubstitute(dto));
                        } else {
                            //TODO Nachrichten
                        }
                    } else {
                        if (element.tagName().equals("p")) {
                            Elements days = element.getElementsByTag("b");
                            if (days.size()>0)
                                currentDate = this.parseDate(days.get(0).text().substring(0, days.get(0).text().lastIndexOf('.')), weeks.get(no));
                        }
                    }
                }
            }
        } catch (IOException | ParseException e) {
            LOGGER.error("Couldn't fetch Substitutions", e);
        }
    }

    private Date parseDate(String dayAndMonth, int year) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");

        return new Date(simpleDateFormat.parse(dayAndMonth + "." + year).getTime());
    }

}
