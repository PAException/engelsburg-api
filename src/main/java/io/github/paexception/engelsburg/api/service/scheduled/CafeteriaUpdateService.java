package io.github.paexception.engelsburg.api.service.scheduled;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.paexception.engelsburg.api.controller.CafeteriaController;
import io.github.paexception.engelsburg.api.endpoint.dto.CafeteriaInformationDTO;
import io.github.paexception.engelsburg.api.util.WordpressAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;

@Service
public class CafeteriaUpdateService {

	@Autowired
	private CafeteriaController cafeteriaController;

	/**
	 * Scheduled function to update cafeteria information.
	 */
	@Scheduled(fixedRate = 10 * 60 * 1000)
	public void updateCafeteriaInformation() {
		DataInputStream input;
		try {
			input = new DataInputStream(
					new URL("https://engelsburg.smmp.de/wp-json/wp/v2/pages/635")
							.openConnection().getInputStream());
			String raw = new String(input.readAllBytes());

			JsonObject json = JsonParser.parseString(raw).getAsJsonObject();
			if (json.get("id") == null) return;

			String link = json.get("link").getAsString();
			String content = json.get("content").getAsJsonObject().get("rendered").getAsString();
			String mediaUrl = WordpressAPI.getFeaturedMedia(json.get("featured_media").getAsInt(), content);

			this.cafeteriaController.update(new CafeteriaInformationDTO(content, link, mediaUrl));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
