package io.github.paexception.engelsburginfrastructure.util;

import io.github.paexception.engelsburginfrastructure.EngelsburgInfrastructureApplication;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashComparator {

	private static MessageDigest digest;

	/**
	 * Checks if an {@code Object} belongs to a specific <i>hash</i>
	 *
	 * @param o to hash
	 * @param hash to compare
	 * @return null if hash belongs to <i>o</i> otherwise the real hash
	 */
	public static String compare(Object o, String hash) {
		String hashed = hash(o);

		return hashed.equals(hash) ? null : hashed;
	}

	public static String hash(Object o) {
		try {
			if (digest == null) digest = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			EngelsburgInfrastructureApplication.getLOGGER().error("Couldn't find defined Algorithm");
		}

		return bytesToHex(digest.digest(o.toString().getBytes()));
	}

	private static String bytesToHex(byte[] hash) {
		StringBuilder hexString = new StringBuilder();
		for (byte b : hash) {
			String hex = Integer.toHexString(0xff & b);
			if (hex.length() == 1) hexString.append('0');
			hexString.append(hex);
		}

		return hexString.toString();
	}

}
