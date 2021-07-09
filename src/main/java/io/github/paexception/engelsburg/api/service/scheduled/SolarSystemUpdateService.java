package io.github.paexception.engelsburg.api.service.scheduled;

import io.github.paexception.engelsburg.api.controller.SolarSystemController;
import io.github.paexception.engelsburg.api.endpoint.dto.SolarSystemDTO;
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
public class SolarSystemUpdateService {

	@Autowired
	private SolarSystemController solarSystemController;

	/**
	 * Scheduled function to update the solar system status every hour.
	 */
	@Scheduled(fixedRate = 10 * 60 * 1000)
	public void updateSolarSystemInfo() {
		try {
			Document doc = Jsoup.connect("https://www.sunnyportal.com/Templates/PublicPageOverview.aspx?plant=554d90c7-84a2-474c-94db-d2ac5f5af3c3&splang=de-de").get();

			String date = doc.getElementById("ctl00_ContentPlaceHolder1_PublicPagePlaceholder_PageUserControl_ctl00_UserControl0_LabelTime").text();
			String energy = doc.getElementById("ctl00_ContentPlaceHolder1_PublicPagePlaceholder_PageUserControl_ctl00_UserControl0_LabelETotalValue").text();
			String co2avoidance = doc.getElementById("ctl00_ContentPlaceHolder1_PublicPagePlaceholder_PageUserControl_ctl00_UserControl0_LabelCO2Value").text();
			String payment = doc.getElementById("ctl00_ContentPlaceHolder1_PublicPagePlaceholder_PageUserControl_ctl00_UserControl0_LabelRevenueValue").text();

			this.solarSystemController.update(new SolarSystemDTO(date, energy, co2avoidance, payment));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
