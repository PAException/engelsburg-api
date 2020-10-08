package io.github.paexception.engelsburg.api.configuration;

import io.github.paexception.engelsburg.api.EngelsburgAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

@Service
public class Configuration {

	private static final Logger LOGGER = LoggerFactory.getLogger(Configuration.class.getSimpleName());
	private static final Properties CONFIGURATION = new Properties();
	private static final File CONFIGURATION_FILE = new File(EngelsburgAPI.DATA_FOLDER + File.separator + "engelsburg-api.properties");

	@EventListener(ApplicationStartingEvent.class)
	public void loadConfig() {
		try {
			FileInputStream input = new FileInputStream(CONFIGURATION_FILE);
			CONFIGURATION.load(input);
			input.close();
		} catch (IOException e) {
			LOGGER.error("Couldn't load config in " + CONFIGURATION_FILE.getPath(), e);
		}
	}

	private static void storeConfig() {
		try {
			FileOutputStream out = new FileOutputStream(CONFIGURATION_FILE);
			CONFIGURATION.store(out, "Last loaded/stored: " + System.currentTimeMillis());
			out.close();
		} catch (IOException e) {
			LOGGER.error("Couldn't store config to " + CONFIGURATION_FILE.getPath(), e);
		}
	}

	public static ConfigurationEntry getConfigEntry(String key) {
		return new ConfigurationEntry(CONFIGURATION.getProperty(key));
	}

	public static void setConfigEntry(String key, Object value) {
		CONFIGURATION.setProperty(key, value.toString());
		storeConfig();
	}

}
