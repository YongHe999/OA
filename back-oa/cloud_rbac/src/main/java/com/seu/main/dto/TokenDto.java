package com.seu.main.dto;

import lombok.Data;

import java.io.Serializable;

/**
 *
 */
@Data
public class TokenDto implements Serializable {

	private static final long serialVersionUID = 6314027741784310221L;

	private String token;
	/** 登陆时间戳（毫秒） */
	private Long loginTime;

	public TokenDto(String token, Long loginTime) {
		super();
		this.token = token;
		this.loginTime = loginTime;
	}
}
