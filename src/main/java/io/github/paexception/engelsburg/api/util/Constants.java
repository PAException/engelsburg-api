package io.github.paexception.engelsburg.api.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

public class Constants {

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Authentication {

		public static final String NAME_KEY = "authentication";

	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Article {

		public static final String NAME_KEY = "article";

	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Information {

		public static final String NAME_KEY = "info_classes";

	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Event {

		public static final String NAME_KEY = "event";

	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Substitute {

		public static final String NAME_KEY = "substitute";

	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class SubstituteMessage {

		public static final String NAME_KEY = "substitute_message";

	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Notification {

		public static final String NAME_KEY = "notification";

	}

}
