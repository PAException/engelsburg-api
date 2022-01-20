package io.github.paexception.engelsburg.api.util.l10n;

/**
 * Custom string class to replace custom placeholders.
 */
public class LocalizationString {

	String string;

	public LocalizationString(String string) {
		this.string = string;
	}

	/**
	 * Replace any placeholder with any object (e.g. String, Integer, Boolean, ...).
	 *
	 * @param placeholder where to replace
	 * @param replacement to replace
	 * @return current instance
	 */
	public LocalizationString placeholder(String placeholder, Object replacement) {
		this.string = this.string.replaceAll("(?i)\\{" + placeholder + "}", replacement.toString());

		return this;
	}

	/**
	 * Get actual string.
	 *
	 * @return string
	 */
	public String get() {
		return this.string;
	}

	@Override
	public String toString() {
		return this.string;
	}
}
