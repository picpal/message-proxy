package com.bwc.common.crypt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EncHashPassword implements PasswordEncoder {
	@Override
	public String encode(CharSequence rawPassword) {
		String salt = generateSalt(16);
		return getSHA256Key(rawPassword.toString(), salt) + "$" + salt; // DB 저장을 위해 Salt를 반환

	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		if (rawPassword == null) {
			throw new IllegalArgumentException("rawPassword cannot be null");
		}
		if (encodedPassword == null || encodedPassword.length() == 0) {
			return false;
		}

		// get origin key & salt
		String[] parts = encodedPassword.split("\\$");
		if (parts.length != 2) {
			return false;
		}
		String hashApiKey = parts[0];
		String salt = parts[1];

		// Hash
		String hashInputApiKey = getSHA256Key(rawPassword.toString(), salt);

		return hashApiKey.equals(hashInputApiKey);
	}

	/**
	 * key와 salt값을 받아 Hash 암호화 처리
	 *
	 * @Params String 암호화 할 key
	 * @Params String Random salt
	 **/
	private String getSHA256Key(String key, String salt) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(salt.getBytes());   // salt
			md.update(key.getBytes());    // key
			byte[] hashKey = md.digest(); // hash

			return Base64.getEncoder().encodeToString(hashKey); // base64 encoding
		} catch (NoSuchAlgorithmException ex) {
			throw new RuntimeException("Failed to Generate Key.", ex);
		}
	}

	/**
	 * Random Salt Text Generator
	 *
	 * @Params int 생성할 문자열 길이
	 **/
	private String generateSalt(int length) {
		SecureRandom r = new SecureRandom();
		StringBuilder sb = new StringBuilder(length);

		for (int i = 0; i < length; i++) {
			if (r.nextBoolean()) {
				// 숫자 추가
				sb.append(r.nextInt(10));
			} else {
				// 알파벳 추가(대,소문자)
				sb.append((char)(r.nextBoolean() ? r.nextInt(26) + 'A' : r.nextInt(26) + 'a'));
			}
		}
		return sb.toString();
	}

}
