/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Constants mostly used to name errors in controllers.
 */
public class Constants {

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Article {

		public static final String NAME_KEY = "article";

	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Event {

		public static final String NAME_KEY = "event";

	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Information {

		public static final String NAME_KEY = "information";

	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class NotificationSettings {

		public static final String NAME_KEY = "notification_settings";

	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Substitute {

		public static final String NAME_KEY = "substitute";

	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class SubstituteMessage {

		public static final String NAME_KEY = "substitute_message";

	}

}
