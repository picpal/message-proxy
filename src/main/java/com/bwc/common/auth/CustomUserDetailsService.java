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

import com.bwc.message.v1.gate.dao.MaMessageDAO;
import com.bwc.message.v1.gate.dto.MessageGateApiKeyResDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

	private final MaMessageDAO maMessageDAO;

	@Autowired
	public CustomUserDetailsService(MaMessageDAO maMessageDAO) {
		this.maMessageDAO = maMessageDAO;
	}

	@Override
	public UserDetails loadUserByUsername(String serviceCode) throws UsernameNotFoundException {
		try {
			// 이전 발행된 KEY 조회 ( @TODO 유예기간 기능 개발 필요, 조회할 때는 salt + 유입된 API key로 hash한 값을 조회조건으로 해야함)
			// MessageGateApiKeyResDTO preRes = maMessageDAO.selectPreApiKey(serviceCode);

			// 현재 발행된 KEY 조회
			MessageGateApiKeyResDTO nowRes = maMessageDAO.selectApiKey(serviceCode);

			// MessageGateApiKeyResDTO res = preRes == null ? nowRes : preRes;
			MessageGateApiKeyResDTO res = nowRes;
			if (res == null || res.getApiKey() == null) {
				throw new UsernameNotFoundException("Service Not found with serviceCode : " + serviceCode);
			}

			return new CustomUserDetails(res.getServiceCode(), res.getApiKey() + "$" + res.getSalt(), getAuthorities());
		} catch (Exception e) {
			// e.printStackTrace();
			log.info("####### [ERROR] API Key Serch Error");
			throw new RuntimeException(e);
		}
	}

	private Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singletonList(new SimpleGrantedAuthority("USER")); // 권한에 따른 Endpoint 구분이 필요한 경우 수정필요
	}

}
