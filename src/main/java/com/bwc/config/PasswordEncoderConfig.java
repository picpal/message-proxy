package com.bwc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.bwc.common.crypt.EncHashPassword;

@Configuration
public class PasswordEncoderConfig {

	@Bean
	public PasswordEncoder passwordEncoder() {
		// 단방향 암호화 , 같은 값이라도 매번 다른 값 반환
		// return new BCryptPasswordEncoder();

		// hash 단방향 암호화
		EncHashPassword enchash = new EncHashPassword();

		// 단방향 Hash 암호화
		return enchash;
	}
}
