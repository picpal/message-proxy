package com.bwc.common.auth;

import java.io.Serializable;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;

@Getter
public class CustomUserDetails implements UserDetails, Serializable {

	private String serviceCode;
	private String apiKey;
	private String useYn;
	private Collection<? extends GrantedAuthority> authorities;

	public CustomUserDetails(String serviceCode, String apiKey, Collection<? extends GrantedAuthority> role) {
		this.serviceCode = serviceCode;
		this.apiKey = apiKey;
		this.authorities = role;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return apiKey;
	}

	@Override
	public String getUsername() {
		return serviceCode;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
