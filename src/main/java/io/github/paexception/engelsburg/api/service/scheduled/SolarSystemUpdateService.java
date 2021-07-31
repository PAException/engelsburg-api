package io.github.paexception.engelsburg.api.service.scheduled;

import io.github.paexception.engelsburg.api.controller.shared.SolarSystemController;
import io.github.paexception.engelsburg.api.endpoint.dto.SolarSystemDTO;
import io.github.paexception.engelsburg.api.service.HtmlFetchingService;
import io.github.paexception.engelsburg.api.util.LoggingComponent;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.io.IOException;

/**
 * Service to update status of solar system.
 */
@Service
public class SolarSystemUpdateService extends HtmlFetchingService implements LoggingComponent {

	private static final Logger LOGGER = LoggerFactory.getLogger(SolarSystemUpdateService.class);
	@Autowired
	private SolarSystemController solarSystemController;

	/**
	 * Scheduled function to update the solar system status.
	 */
	@Scheduled(fixedRate = 5 * 60 * 1000)
	public void updateSolarSystemInfo() {
		LOGGER.debug("Starting to fetch solar system information");
		try {
			Document doc = this.request("https://www.sunnyportal.com/Templates/PublicPageOverview.aspx?plant=554d90c7-84a2-474c-94db-d2ac5f5af3c3&splang=de-de");

			if (this.checkChanges(doc.getElementById("ctl00_ContentPlaceHolder1_PublicPagePlaceholder_PageUserControl_ctl00_UpdatePanel0"))) {
				String date = doc.getElementById("ctl00_ContentPlaceHolder1_PublicPagePlaceholder_PageUserControl_ctl00_UserControl0_LabelTime").text();
				String energy = doc.getElementById("ctl00_ContentPlaceHolder1_PublicPagePlaceholder_PageUserControl_ctl00_UserControl0_LabelETotalValue").text();
				String co2avoidance = doc.getElementById("ctl00_ContentPlaceHolder1_PublicPagePlaceholder_PageUserControl_ctl00_UserControl0_LabelCO2Value").text();
				String payment = doc.getElementById("ctl00_ContentPlaceHolder1_PublicPagePlaceholder_PageUserControl_ctl00_UserControl0_LabelRevenueValue").text();

				this.solarSystemController.update(new SolarSystemDTO(date, energy, co2avoidance, payment));
				LOGGER.info("Updated solar system information");
			} else LOGGER.debug("Solar system information has not changed");
		} catch (IOException e) {
			this.logError("Couldn't fetch solar system information", e, LOGGER);
		}
	}

}
