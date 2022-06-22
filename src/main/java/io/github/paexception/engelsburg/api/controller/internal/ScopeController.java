/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.controller.internal;

import com.google.common.collect.Lists;
import io.github.paexception.engelsburg.api.database.model.ScopeModel;
import io.github.paexception.engelsburg.api.database.model.user.UserModel;
import io.github.paexception.engelsburg.api.database.repository.ScopeRepository;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * Controller for scopes.
 */
@Component
@AllArgsConstructor
public class ScopeController {

	//List of default scopes to grant to user by creating an account
	public static final List<String> DEFAULT_SCOPES = List.of(
			"notification.settings.write.self",
			"notification.settings.read.self",

			"user.data.read.self",
			"user.data.delete.self"
	);
	//List of scopes to grant to user by verifying their email address
	public static final List<String> VERIFIED_SCOPES = List.of(
			"article.save.write.self",
			"article.save.delete.self",
			"article.save.read.self",

			"grade.write.self",
			"grade.read.self",
			"grade.delete.self",

			"grade.share.write.self",
			"grade.share.read.self",
			"grade.share.delete.self",

			"semester.write.self",
			"semester.read.self",
			"semester.delete.self",

			"subject.write.self",
			"subject.read.self",
			"subject.delete.self",

			"task.delete.self",
			"task.write.self",
			"task.read.self",

			"timetable.write.self",
			"timetable.read.self",
			"timetable.delete.self"
	);

	private final ScopeRepository scopeRepository;

	/**
	 * Concat multiple scopes to one long string to reduce JWT size.
	 *
	 * <p>The merging occurs like the following:</p>
	 * <p>On the same level ({@code '+'}):
	 * {@code example.read.self} and {@code example.write.self} would be merged to {@code example.read+write.self}</p>
	 * <p>The {@code '.'} marks a step into, the {@code '-'} a step out:
	 * {@code example.read.self} and {@code example.write.all} would be merged to {@code example.read.self-write.self}</p>
	 * These scopes would look hierarchic like this:
	 * <p>example</p>
	 * <p>- read</p>
	 * <p>- - self</p>
	 * <p>- write</p>
	 * <p>- - all</p>
	 *
	 * @param value Scopes to merge
	 * @return String with merged scopes
	 */
	public static String mergeScopes(String[] value) {
		//Return empty string if array is empty
		if (value == null || value.length == 0) return "";

		//Create and sort ArrayList of scopes
		List<String> scopes = Lists.newArrayList(value);
		scopes.sort(String::compareToIgnoreCase);

		StringBuilder builder = new StringBuilder();
		int lastDepth = 0;

		for (int i = 0; i < scopes.size(); i++) {
			String scope = scopes.get(i);
			int currentDepth = 0;

			if (i != 0) {
				String[] split2 = scopes.get(i - 1).split("\\.");
				int lastIndex1 = 0;

				for (int j = 0; j < Math.min(StringUtils.countMatches(scope, "."), split2.length); j++) {
					lastIndex1 = j == 0 ? 0 : scope.indexOf(".", lastIndex1) + 1;
					String sub1 = scope.substring(lastIndex1);

					if (sub1.startsWith(split2[j])) {
						currentDepth++;
					} else {
						int depthDiff = lastDepth - split2.length + currentDepth - 2;

						if (depthDiff == 0) {
							builder.append("+");
						} else if (depthDiff > 0) {
							builder.append(".".repeat(depthDiff));
						} else {
							builder.append("-".repeat((depthDiff) * (-1)));
						}
						builder.append(sub1);
						break;
					}
				}
			} else {
				lastDepth = scope.split("\\.").length - 1;
				builder.append(scope);
			}
		}

		return builder.toString();
	}

	/**
	 * Check if a scope is encoded into a concatenated string.
	 *
	 * @param scope  to search for
	 * @param scopes to search into (concatenated string)
	 * @return true if concatenated string contains scope, false otherwise
	 */
	public static boolean hasScope(String scope, String scopes) {
		String[] split = scope.split("\\.");
		String sub = scopes;
		boolean found = true;
		for (int i = 0; i < split.length && found; i++) {
			String part = split[i];
			if (sub.contains(part)) {
				boolean cascadedFound = false;
				int index = -1;
				while (!cascadedFound && (index = sub.indexOf(part, index + 1)) != -1) {
					String pre = sub.substring(0, index);
					if (StringUtils.countMatches(pre, ".") == StringUtils.countMatches(pre, "-") - (i == 0 ? 0 : -1)) {
						cascadedFound = true;
					}
				}
				if (!cascadedFound) found = false;
				if ((index + part.length()) <= sub.length() - 1) {
					char c = sub.charAt(index + part.length());
					if (c != '+' && c != '.' && c != '-') {
						found = false;
					}
				}
				sub = sub.substring(sub.indexOf(part));
			} else {
				found = false;
			}
		}

		return found;
	}

	/**
	 * Add a scope to a user.
	 *
	 * @param user  to search for
	 * @param scope to add
	 */
	public void addScope(UserModel user, String scope) {
		this.scopeRepository.save(new ScopeModel(-1, user, scope));
	}

	/**
	 * Add default scopes to a user.
	 *
	 * @param user to add scopes to
	 */
	public void addDefaultScopes(UserModel user) {
		DEFAULT_SCOPES.forEach(scope -> this.addScope(user, scope));
	}


	/**
	 * Add verified scopes to a user.
	 *
	 * @param user to add scopes to
	 */
	public void addVerifiedScopes(UserModel user) {
		VERIFIED_SCOPES.forEach(scope -> this.addScope(user, scope));
	}

	/**
	 * Update scopes of a user.
	 *
	 * @param user   to search for
	 * @param scopes to update if not existing
	 */
	public void updateScopes(UserModel user, List<String> scopes) {
		scopes.forEach(scope -> {
			if (!this.scopeRepository.existsByUserAndScope(user, scope))
				this.scopeRepository.save(new ScopeModel(-1, user, scope));
		});
	}

	/**
	 * Add scopes to users if the default or verified ones have changed.
	 *
	 * @param all      Add default scopes
	 * @param verified Add verified scopes
	 */
	public void updateDefaultScopes(List<UserModel> all, List<UserModel> verified) {
		for (UserModel user : all) this.updateScopes(user, DEFAULT_SCOPES);
		for (UserModel user : verified) this.updateScopes(user, VERIFIED_SCOPES);
	}

	/**
	 * Get all scopes of a user.
	 *
	 * @param user to search for
	 * @return scopes as String[]
	 */
	public String[] getScopes(UserModel user) {
		return this.scopeRepository.findAllByUser(user).stream().map(ScopeModel::getScope).toArray(String[]::new);
	}
}
