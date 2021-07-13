package io.github.paexception.engelsburg.api.service.scheduled;

import io.github.paexception.engelsburg.api.controller.shared.SolarSystemController;
import io.github.paexception.engelsburg.api.endpoint.dto.SolarSystemDTO;
import io.github.paexception.engelsburg.api.util.LoggingComponent;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.io.IOException;

/**
 * Service to update status of solar system.
 */
@Service
public class SolarSystemUpdateService extends LoggingComponent {

	@Autowired
	private SolarSystemController solarSystemController;

	public SolarSystemUpdateService() {
		super(SolarSystemUpdateService.class);
	}

	/**
	 * Scheduled function to update the solar system status.
	 */
	@Scheduled(fixedRate = 10 * 60 * 1000)
	public void updateSolarSystemInfo() {
		this.logger.debug("Starting to fetch solar system information");
		try {
			Document doc = Jsoup.connect("https://www.sunnyportal.com/Templates/PublicPageOverview.aspx?plant=554d90c7-84a2-474c-94db-d2ac5f5af3c3&splang=de-de").get();

			String date = doc.getElementById("ctl00_ContentPlaceHolder1_PublicPagePlaceholder_PageUserControl_ctl00_UserControl0_LabelTime").text();
			String energy = doc.getElementById("ctl00_ContentPlaceHolder1_PublicPagePlaceholder_PageUserControl_ctl00_UserControl0_LabelETotalValue").text();
			String co2avoidance = doc.getElementById("ctl00_ContentPlaceHolder1_PublicPagePlaceholder_PageUserControl_ctl00_UserControl0_LabelCO2Value").text();
			String payment = doc.getElementById("ctl00_ContentPlaceHolder1_PublicPagePlaceholder_PageUserControl_ctl00_UserControl0_LabelRevenueValue").text();

			this.solarSystemController.update(new SolarSystemDTO(date, energy, co2avoidance, payment));
			this.logger.info("Updated solar system information");
		} catch (IOException e) {
			this.logError("Couldn't fetch solar system information", e);
		}
	}

}
