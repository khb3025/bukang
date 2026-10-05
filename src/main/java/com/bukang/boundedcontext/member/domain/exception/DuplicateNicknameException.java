package com.bukang.boundedcontext.member.domain.exception;

public class DuplicateNicknameException extends RuntimeException {
	public DuplicateNicknameException(String message) {
		super(message);
	}
}
