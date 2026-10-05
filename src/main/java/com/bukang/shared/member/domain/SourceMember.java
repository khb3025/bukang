package com.bukang.shared.member.domain;

import static jakarta.persistence.GenerationType.*;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 회원 Context가 원본으로 관리하는 회원 (ID 자동 생성, 생성/수정 시각 자동 기록)
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor
public abstract class SourceMember extends BaseMember {
	@Id
	@GeneratedValue(strategy = IDENTITY)
	private int id;
	@CreatedDate
	private LocalDateTime createDate;
	@LastModifiedDate
	private LocalDateTime modifyDate;

	public SourceMember(
		String username,
		String password,
		String nickname,
		String email,
		String phone,
		String phoneHash
	) {
		super(username, password, nickname, email, phone, phoneHash);
	}
}
