package io.github.paexception.engelsburg.api.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class FetchingService {

	private static MessageDigest digest;
	private final Map<String, byte[]> currentHash = new HashMap<>();

	private static byte[] hash(Object o) {
		if (o == null) return null;
		try {
			if (digest == null) digest = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException ignored) {
		}

		return digest.digest(o.toString().getBytes());
	}

	protected abstract Object request(String url) throws Exception;

	protected final boolean checkChanges(Object o) {
		return this.checkChanges(o, "root");
	}

	protected final boolean checkChanges(Object o, String key) {
		byte[] hash = hash(o);

		if (Arrays.equals(hash, this.currentHash.get(key)) || hash == null) return false;
		else {
			this.currentHash.put(key, hash);
			return true;
		}
	}

}
