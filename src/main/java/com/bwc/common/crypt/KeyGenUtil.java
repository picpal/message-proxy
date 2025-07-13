package com.bwc.common.crypt;

import java.security.SecureRandom;

import org.apache.commons.codec.binary.Hex;

public class KeyGenUtil {

	public static String generateSecretKey() {
		byte[] iv = new byte[10];
		new SecureRandom().nextBytes(iv);
		return Hex.encodeHexString(iv);
	}

	public static String generateMrchCode() {
		byte[] iv = new byte[10];
		// brand code
		iv = new byte[3];
		new SecureRandom().nextBytes(iv);
		return Hex.encodeHexString(iv) + longToBase64(System.nanoTime());
	}

	public static String longToBase64(long v) {
		final char[] digits = {
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
			'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
			'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
			'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D',
			'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
			'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
			'Y', 'Z', '-', '_'
		};

		int shift = 6;
		char[] buf = new char[64];
		int charPos = 64;
		int radix = 1 << shift;
		long mask = radix - 1;
		long number = v;

		do {
			buf[--charPos] = digits[(int)(number & mask)];
			number >>>= shift;
		} while (number != 0);

		return new String(buf, charPos, (64 - charPos));

	}
}
