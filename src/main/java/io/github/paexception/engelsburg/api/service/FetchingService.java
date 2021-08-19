package io.github.paexception.engelsburg.api.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Implement to allow services to unify requests and to check for changes.
 */
public abstract class FetchingService {

	private static MessageDigest digest;
	private final Map<String, byte[]> currentHash = new HashMap<>();

	/**
	 * Hash an object.
	 *
	 * @param o to hash
	 * @return hash
	 */
	private static byte[] hash(Object o) {
		if (o == null) return null;
		try {
			if (digest == null) digest = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException ignored) {
		}

		return digest.digest(o.toString().getBytes());
	}

	/**
	 * Inherited to unify requests of different types of services.
	 *
	 * @param url to request
	 * @return response of request (can be parsed)
	 * @throws Exception if any error occurs
	 */
	protected abstract Object request(String url) throws Exception;

	protected final boolean checkChanges(Object o) {
		return this.checkChanges(o, "root");
	}

	/**
	 * Check for changes of something.
	 *
	 * @param o   to check for changes
	 * @param key if many objects are checked for changes
	 * @return true of changes occurred, false otherwise
	 */
	protected final boolean checkChanges(Object o, String key) {
		byte[] hash = hash(o);

		if (Arrays.equals(hash, this.currentHash.get(key)) || hash == null) return false;
		else {
			this.currentHash.put(key, hash);
			return true;
		}
	}

}
