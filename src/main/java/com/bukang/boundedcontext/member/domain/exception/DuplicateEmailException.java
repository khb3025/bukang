package com.bukang.boundedcontext.member.domain.exception;

public class DuplicateEmailException extends RuntimeException {
	public DuplicateEmailException(String message) {
		super(message);
	}
}
