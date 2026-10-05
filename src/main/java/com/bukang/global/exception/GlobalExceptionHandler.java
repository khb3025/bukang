package com.bukang.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.bukang.boundedcontext.member.domain.exception.DuplicateEmailException;
import com.bukang.boundedcontext.member.domain.exception.DuplicateNicknameException;
import com.bukang.boundedcontext.member.domain.exception.DuplicatePhoneException;
import com.bukang.global.rsdata.RsData;
import com.bukang.shared.member.exception.MemberNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(DuplicateEmailException.class)
	public ResponseEntity<RsData<Void>> duplicateEmailException(DuplicateEmailException exception) {
		return ResponseEntity
			.status(HttpStatus.CONFLICT)
			.body(RsData.of(HttpStatus.CONFLICT, exception.getMessage(), null));
	}

	@ExceptionHandler(DuplicateNicknameException.class)
	public ResponseEntity<RsData<Void>> duplicateNicknameException(DuplicateNicknameException exception) {
		return ResponseEntity
			.status(HttpStatus.CONFLICT)
			.body(RsData.of(HttpStatus.CONFLICT, exception.getMessage(), null));
	}

	@ExceptionHandler(DuplicatePhoneException.class)
	public ResponseEntity<RsData<Void>> duplicatePhoneException(DuplicatePhoneException exception) {
		return ResponseEntity
			.status(HttpStatus.CONFLICT)
			.body(RsData.of(HttpStatus.CONFLICT, exception.getMessage(), null));
	}

	@ExceptionHandler(MemberNotFoundException.class)
	public ResponseEntity<RsData<Void>> memberNotFoundException(MemberNotFoundException exception) {
		return ResponseEntity
			.status(HttpStatus.NOT_FOUND)
			.body(RsData.of(HttpStatus.NOT_FOUND, exception.getMessage(), null));
	}

	// @Valid 검증 실패: 첫 번째 필드 오류 메시지를 응답한다
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<RsData<Void>> methodArgumentNotValidException(MethodArgumentNotValidException exception) {
		String message = exception.getBindingResult().getFieldErrors().stream()
			.findFirst()
			.map(fieldError -> fieldError.getDefaultMessage())
			.orElse("요청 값이 올바르지 않습니다.");

		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(RsData.of(HttpStatus.BAD_REQUEST, message, null));
	}

	// 요청 본문(JSON) 형식 오류: 처리하지 않으면 /error로 넘어가 인증 오류(401)처럼 응답된다
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<RsData<Void>> httpMessageNotReadableException(HttpMessageNotReadableException exception) {
		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(RsData.of(HttpStatus.BAD_REQUEST, "요청 본문 형식이 올바르지 않습니다.", null));
	}

	// 로그인 실패: 이메일이 없는 경우와 비밀번호가 틀린 경우를 구분하지 않는다
	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<RsData<Void>> authenticationException(AuthenticationException exception) {
		return ResponseEntity
			.status(HttpStatus.UNAUTHORIZED)
			.body(RsData.of(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다.", null));
	}
}
