package io.github.paexception.engelsburg.api.configuration;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@AllArgsConstructor
public class ConfigurationEntry {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationEntry.class.getSimpleName());

	private final String entry;

	public boolean getAsBoolean() {
		return Boolean.parseBoolean(this.entry);
	}

	public byte getAsByte() {
		try {
			return Byte.parseByte(this.entry);
		} catch (NumberFormatException e) {
			throwNumberFormatException(e);
			return 0;
		}
	}

	public short getAsShort() {
		try {
			return Short.parseShort(this.entry);
		} catch (NumberFormatException e) {
			throwNumberFormatException(e);
			return 0;
		}
	}

	public int getAsInt() {
		try {
			return Integer.parseInt(this.entry);
		} catch (NumberFormatException e) {
			throwNumberFormatException(e);
			return 0;
		}
	}

	public long getAsLong() {
		try {
			return Long.parseLong(this.entry);
		} catch (NumberFormatException e) {
			throwNumberFormatException(e);
			return 0;
		}
	}

	public float getAsFloat() {
		try {
			return Float.parseFloat(this.entry);
		} catch (NumberFormatException e) {
			throwNumberFormatException(e);
			return 0;
		}
	}

	public double getAsDouble() {
		try {
			return Double.parseDouble(this.entry);
		} catch (NumberFormatException e) {
			throwNumberFormatException(e);
			return 0;
		}
	}

	public char getAsChar() {
		return this.entry.length() > 0 ? this.entry.charAt(0) : Character.MIN_VALUE;
	}

	public String getAsString() {
		return this.entry;
	}

	private void throwNumberFormatException(NumberFormatException e) {
		LOGGER.error("A ConfigurationEntry's value was a different type than excepted", e);
	}

}
