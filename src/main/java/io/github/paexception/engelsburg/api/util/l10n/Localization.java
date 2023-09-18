/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.util.l10n;

import com.google.gson.Gson;
import io.github.paexception.engelsburg.api.util.LoggingComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Class which handles localized strings.
 */
@Component
public class Localization implements LoggingComponent {

	private static final Logger LOGGER = LoggerFactory.getLogger(Localization.class);
	private static final Map<String, Map<String, String>> L10N = new HashMap<>();
	private static final Gson GSON = new Gson();

	/**
	 * Get localized string.
	 *
	 * @param langCode in [lang]_[country] code format
	 * @param key      of localized string
	 * @return localized string
	 */
	public static LocalizationString string(String langCode, String key) {
		String actualLangcode;
		if (!L10N.containsKey(langCode)) {
			actualLangcode = L10N.keySet().stream()
					.filter(s -> s.startsWith(langCode.split("_")[0]))
					.findFirst().orElse(null);
			if (actualLangcode == null) return new LocalizationString("Missing language: " + langCode);
		} else actualLangcode = langCode;

		return new LocalizationString(Objects.requireNonNullElse(
				L10N.get(actualLangcode).get(key),
				"Missing localized message: [" + actualLangcode + "," + key + "]"
		));
	}

	/**
	 * Add all localization messages.
	 */
	@Bean
	private void initialize() {
		try {
			LOGGER.debug("[Localization] Starting to load messages");
			for (String file : this.getL10nFilenames()) {
				Map<String, String> map = new HashMap<>(); //Create map to put strings
				InputStreamReader in = new InputStreamReader(this.getResource("l10n" + File.separator + file));
				Map<?, ?> json = GSON.fromJson(in, Map.class); //Get json
				String lang = file.replace(".arb", "");
				LOGGER.trace("[Localization] Loading language: " + lang);

				for (Object o1 : json.keySet()) { //Add all entries which don't start with @
					if (!(o1 instanceof String)) continue; //Casting checks
					String key = (String) o1;
					if (key.startsWith("@")) continue; //Return if @

					Object o2 = json.get(key);
					if (!(o2 instanceof String)) continue; //Casting checks

					map.put(key, (String) o2); //Put in map
				}
				L10N.put(lang, map);
				LOGGER.trace("[Localization] Successful loaded language: " + lang + " (" + map.size() + " messages)");
				in.close();
			}
			if (L10N.isEmpty()) LOGGER.warn("[Localization] Didn't load any localization languages");
			else LOGGER.info("[Localization] Loaded messages");
		} catch (IOException e) {
			this.logError("[Localization] Couldn't load files", e, LOGGER);
		}
	}

	/**
	 * Get any classpath resource as stream.
	 *
	 * @param resource to get stream from
	 * @return stream
	 */
	private InputStream getResource(String resource) throws IOException {
		return new ClassPathResource(resource).getInputStream();
	}

	/**
	 * Get all filenames of l10n files.
	 *
	 * @return filenames
	 * @throws IOException if reading fails
	 */
	private List<String> getL10nFilenames() throws IOException {
		List<String> filenames = new ArrayList<>();

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(this.getResource("l10n")))) {
			String file;
			while ((file = reader.readLine()) != null) filenames.add(file);
		}

		return filenames;
	}


}
