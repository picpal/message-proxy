package com.bwc.common.crypt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncSHA256 {
	public static String doCryption(String input) {

		if (input == null || input.equals("")) {
			return input;
		}

		//String SHA = "";
		StringBuffer sb = new StringBuffer();
		StringBuffer hexString = new StringBuffer();

		try {

			MessageDigest sh = MessageDigest.getInstance("SHA-256");

			sh.update(input.getBytes());

			byte byteData[] = sh.digest();

			//기본 으로 변경
			for (int i = 0; i < byteData.length; i++) {
				sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
			}

			for (int i = 0; i < byteData.length; i++) {
				String hex = Integer.toHexString(0xff & byteData[i]);

				if (hex.length() == 1) {
					hexString.append('0');
				}
				hexString.append(hex);
			}
			//여기까지

			//커스터마이징 부분
			//SHA = toPrintable( byteData);

		} catch (NoSuchAlgorithmException e) {
			hexString = null;
		}
		//return SHA;
		return hexString.toString();

	}

	public static String toPrintable(byte[] in) {
		int length = in.length;
		int padlength = 0;
		String Printable = "";

		if ((length % 3) == 0)
			padlength = 0;
		else if ((length % 3) == 1)
			padlength = 2;
		else if ((length % 3) == 2)
			padlength = 1;

		byte[] input = new byte[length + padlength];
		System.arraycopy(in, 0, input, 0, length);

		if (padlength == 1) {
			input[length] = 0x00;
		} else if (padlength == 2) {
			input[length] = 0x00;
			input[length + 1] = 0x00;
		}

		byte[] part = new byte[3];
		for (int i = 0; i < input.length; i += 3) {
			System.arraycopy(input, i, part, 0, 3);
			byte one = (byte)((part[0] >> 2) & 0x3f);
			byte two = (byte)(((part[0] << 4) & 0x30) | ((part[1] >> 4) & 0x0f));
			byte thr = (byte)(((part[1] << 2) & 0x3c) | ((part[2] >> 6) & 0x03));
			byte fou = (byte)(part[2] & 0x3f);

			Printable = Printable + change(one) + change(two)
				+ change(thr) + change(fou);
		}

		if (padlength == 1) {
			Printable = Printable.substring(0, Printable.length() - 1);
			Printable += "=";
		} else if (padlength == 2) {
			Printable = Printable.substring(0, Printable.length() - 2);
			Printable += "==";
		}

		return Printable;
	}

	private static String[] chartable = {
		"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
		"N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
		"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m",
		"n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
		"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "+", "/"};

	private static String change(byte n) {
		return chartable[(int)n];
	}

}
