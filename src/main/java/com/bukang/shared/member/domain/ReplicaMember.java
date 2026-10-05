package com.bukang.shared.member.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 다른 Context가 회원 이벤트로 복제해 보관하는 회원 정보 (ID와 생성/수정 시각은 원본 회원 값을 그대로 사용)
 */
@MappedSuperclass
@Getter
@NoArgsConstructor
public abstract class ReplicaMember extends BaseMember {
	@Id
	private int id;
	private LocalDateTime createDate;
	private LocalDateTime modifyDate;

	public ReplicaMember(
		int id,
		LocalDateTime createDate,
		LocalDateTime modifyDate,
		String username,
		String password,
		String nickname,
		String email,
		String phone,
		String phoneHash
	) {
		super(username, password, nickname, email, phone, phoneHash);
		this.id = id;
		this.createDate = createDate;
		this.modifyDate = modifyDate;
	}
}
