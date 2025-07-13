package com.bwc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import com.bwc.common.auth.CustomUserDetailsService;

import jakarta.servlet.DispatcherType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	private final CustomUserDetailsService customUserDetailsService;

	public WebSecurityConfig(CustomUserDetailsService customUserDetailsService) {
		this.customUserDetailsService = customUserDetailsService;
	}

	private static final String[] WHITE_LIST = {
		"/v1/**",
		// "/docs/index.html",
		"/**"
	};

	@Bean
	protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.cors(AbstractHttpConfigurer::disable)
			.csrf(AbstractHttpConfigurer::disable) // UI가 없는 경우 일반적으로 disable
			.securityMatcher("/**")
			.formLogin(AbstractHttpConfigurer::disable)
			.sessionManagement(sessionManagementConfigurer -> sessionManagementConfigurer.sessionCreationPolicy(
				SessionCreationPolicy.STATELESS)) // 세션 유지 정책 (세션유지X)
			.authorizeHttpRequests(request -> request
				.dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll()
				// .requestMatchers(WHITE_LIST).permitAll() // 인증 없이 접근 가능한 endpoint 선언
				// .requestMatchers("/v1/admin").hasRole("ADMIN") // ADMIN 권한만 접근 가능한 페이지 지정
				// .requestMatchers("/v1/user").hasRole("USER") // USER 권한만 접근 가능한 페이지 지정
				.anyRequest().authenticated() // WHITE_LIST가 아닌 모든 요청 인증 진행
			)
			.userDetailsService(customUserDetailsService)
			.httpBasic();

		/*
			로그인 기능이 있는 경우 주석 해제하여 페이지 설정
		* */
		// .formLogin(login -> login
		// 	.loginPage("/web/login") // 커스텀 로그인 페이지 지정
		// 	.loginProcessingUrl("login-process") // submit 받을 endpoint
		// 	.usernameParameter("userId") // submit user id parameter , tag의 name 기준
		// 	.usernameParameter("password") // submit user password parameter , tag의 name 기준
		// 	.defaultSuccessUrl("/web/main", true) // 성공시 이동할 페이지
		// 	.permitAll()
		// )
		// .logout(Customizer.withDefaults())
		// ;

		return http.build();
	}

}
