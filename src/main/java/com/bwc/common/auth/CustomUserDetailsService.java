package com.bwc.common.auth;

import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// import com.bwc.message.v1.gate.dao.MaMessageDAO; // 제거된 클래스
// import com.bwc.message.v1.gate.dto.MessageGateApiKeyResDTO; // 제거된 클래스

import lombok.extern.slf4j.Slf4j;

@Slf4j
// @Service // TODO: API Key 인증이 필요한 경우 재구현 필요
public class CustomUserDetailsService implements UserDetailsService {

	// TODO: API Key 인증이 필요한 경우 DAO 및 DTO 재구현 필요
	// private final SomeApiKeyRepository apiKeyRepository;

	public CustomUserDetailsService() {
		// 기본 생성자
	}

	@Override
	public UserDetails loadUserByUsername(String serviceCode) throws UsernameNotFoundException {
		// TODO: 현재 프로젝트에서는 API Key 인증 미구현
		// 필요시 새로운 구조에 맞게 재구현
		throw new UsernameNotFoundException("API Key authentication not implemented in current version");
	}

	private Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singletonList(new SimpleGrantedAuthority("USER")); // 권한에 따른 Endpoint 구분이 필요한 경우 수정필요
	}

}
