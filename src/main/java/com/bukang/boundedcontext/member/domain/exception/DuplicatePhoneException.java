package com.bukang.boundedcontext.member.domain.exception;

public class DuplicatePhoneException extends RuntimeException {
	public DuplicatePhoneException(String message) {
		super(message);
	}
}
