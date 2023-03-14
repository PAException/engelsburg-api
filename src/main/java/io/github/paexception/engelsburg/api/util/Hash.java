/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Util class for several hashes.
 */
public class Hash {

	private static MessageDigest sha1;
	private static MessageDigest sha256;

	/**
	 * Check if MessageDigests were initialized.
	 * If they weren't initialize them.
	 */
	private static void initialize() {
		try {
			if (sha1 == null) sha1 = MessageDigest.getInstance("SHA-1");
			if (sha256 == null) sha256 = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException ignored) { //Impossible
		}
	}

	/**
	 * Create an SHA-1 hash of an object.
	 *
	 * @param obj to hash
	 * @return created hash
	 */
	public static byte[] sha1(Object obj) {
		if (obj == null) return new byte[0];
		initialize();

		return sha1.digest(obj.toString().getBytes());
	}


	/**
	 * Create an SHA-256 hash of an object.
	 *
	 * @param obj to hash
	 * @return created hash
	 */
	public static byte[] sha256(Object obj) {
		if (obj == null) return new byte[0];
		initialize();
		return sha256.digest(obj.toString().getBytes());
	}
}
