package com.bukang.shared.member.exception;

public class MemberNotFoundException extends RuntimeException {
	public MemberNotFoundException(String message) {
		super(message);
	}
}
