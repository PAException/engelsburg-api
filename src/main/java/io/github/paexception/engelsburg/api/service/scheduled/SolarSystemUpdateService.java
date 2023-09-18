/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.service.scheduled;

import com.google.gson.JsonParser;
import io.github.paexception.engelsburg.api.controller.shared.SolarSystemController;
import io.github.paexception.engelsburg.api.service.HtmlFetchingService;
import io.github.paexception.engelsburg.api.util.LoggingComponent;
import lombok.AllArgsConstructor;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.io.DataInputStream;
import java.net.URL;

/**
 * Service to update status of solar system.
 */
@Service
@AllArgsConstructor
public class SolarSystemUpdateService extends HtmlFetchingService implements LoggingComponent {

	private static final Logger LOGGER = LoggerFactory.getLogger(SolarSystemUpdateService.class);
	private final SolarSystemController solarSystemController;

	/**
	 * Scheduled function to update the solar system status.
	 */
	@Scheduled(fixedRate = 5 * 60 * 1000)
	public void updateSolarSystemInfo() {
		if ("false".equals(System.getProperty("app.scheduling.enable"))) return;
		LOGGER.debug("[SOLAR] Fetching...");
		try {
			Document doc = this.request(
					"https://www.sunnyportal.com/Templates/PublicPageOverview.aspx?plant=554d90c7-84a2-474c-94db-d2ac5f5af3c3&splang=de-de");

			if (this.checkChanges(doc.getElementById(
					"ctl00_ContentPlaceHolder1_PublicPagePlaceholder_PageUserControl_ctl00_UpdatePanel0"), "data")) {
				String date = doc.getElementById(
						"ctl00_ContentPlaceHolder1_PublicPagePlaceholder_PageUserControl_ctl00_UserControl0_LabelTime").text();
				String energy = doc.getElementById(
						"ctl00_ContentPlaceHolder1_PublicPagePlaceholder_PageUserControl_ctl00_UserControl0_LabelETotalValue").text();
				String co2avoidance = doc.getElementById(
						"ctl00_ContentPlaceHolder1_PublicPagePlaceholder_PageUserControl_ctl00_UserControl0_LabelCO2Value").text();
				String payment = doc.getElementById(
						"ctl00_ContentPlaceHolder1_PublicPagePlaceholder_PageUserControl_ctl00_UserControl0_LabelRevenueValue").text();

				this.solarSystemController.update(date, energy, co2avoidance, payment);
				LOGGER.info("[SOLAR] Updated");
			} else LOGGER.debug("[SOLAR] Not changed");

			String fetched = new String(new DataInputStream(new URL("https://engelsburg.smmp.de/wp-json/wp/v2/pages/68")
					.openConnection().getInputStream()).readAllBytes());

			if (fetched.length() == 2) return;

			String html = JsonParser.parseString(fetched).getAsJsonObject().get("content").getAsJsonObject().get(
					"rendered").getAsString();

			if (this.checkChanges(html, "text")) this.solarSystemController.updateText(html);
		} catch (Exception e) { //IO and NullPointer
			this.logError("[SOLAR] Couldn't fetch", e, LOGGER);
		}
	}

}
