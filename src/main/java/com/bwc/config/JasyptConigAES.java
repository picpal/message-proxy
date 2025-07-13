package com.bwc.config;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableEncryptableProperties
@Slf4j
public class JasyptConigAES {
	public String jasyptEncryptorPassword =
		System.getProperty("jasypt.opt.pre") + System.getProperty("jasypt.opt.post");

	@Bean("jasyptStringEncryptor")
	public StringEncryptor stringEncryptor() {
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		SimpleStringPBEConfig config = new SimpleStringPBEConfig();
		config.setPassword("MjAyNDAyMDYwODQ1MzE="); //암호화키
		// config.setPassword(jasyptEncryptorPassword); //암호화키
		config.setAlgorithm("PBEWITHHMACSHA512ANDAES_256"); //알고리즘
		config.setKeyObtentionIterations("1000"); //반복할 해싱 함수
		config.setPoolSize("1"); //인스턴스 POOL
		//config.setProviderName("SunJCE");
		config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator"); //salt 생성 클래스
		config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
		config.setStringOutputType("base64"); //인코딩 방식
		encryptor.setConfig(config);

		return encryptor;
	}
}
