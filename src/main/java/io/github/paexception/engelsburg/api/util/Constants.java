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
	public static class ArticleSave {

		public static final String NAME_KEY = "article_save";

	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Event {

		public static final String NAME_KEY = "event";

	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Grade {

		public static final String NAME_KEY = "grade";

	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class GradeShare {

		public static final String NAME_KEY = "grade_share";

	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Information {

		public static final String NAME_KEY = "information";

	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class NotificationDevice {

		public static final String NAME_KEY = "notification_device";

	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class NotificationSettings {

		public static final String NAME_KEY = "notification_settings";

	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Semester {

		public static final String NAME_KEY = "semester";

	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Subject {

		public static final String NAME_KEY = "subject";

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
	public static class Task {

		public static final String NAME_KEY = "task";

	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Timetable {

		public static final String NAME_KEY = "timetable";

	}

}
