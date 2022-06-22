package com.seu.main.dto;

import lombok.Data;

import java.io.Serializable;
@Data
public class ResponseInfo implements Serializable {

	private static final long serialVersionUID = -4417715614021482064L;

	private Integer code;
	private String message;

	public ResponseInfo(Integer code, String message) {
		super();
		this.code = code;
		this.message = message;
	}
}
