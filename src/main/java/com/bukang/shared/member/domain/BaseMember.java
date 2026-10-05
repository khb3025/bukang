package com.bukang.shared.member.domain;

import static lombok.AccessLevel.*;

import com.bukang.global.config.crypto.EncryptedStringConverter;
import com.bukang.global.jpa.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 회원 Context(SourceMember)와 다른 Context(ReplicaMember)가 공유하는 회원 공통 필드
 */
@MappedSuperclass
@Getter
@Setter(value = PROTECTED)
@NoArgsConstructor
public abstract class BaseMember extends BaseEntity {
	@Column(unique = true)
	private String username;
	private String password;
	@Column(unique = true)
	private String nickname;
	@Column(unique = true)
	private String email;

	@Convert(converter = EncryptedStringConverter.class) // DB 저장 시 AES 암호화, 조회 시 복호화
	@Column(length = 255)
	private String phone;

	@Column(name = "phone_hash", unique = true, length = 64) // 휴대폰 번호 조회, 중복 검사용 블라인드 인덱스
	private String phoneHash;

	public BaseMember(
		String username,
		String password,
		String nickname,
		String email,
		String phone,
		String phoneHash
	) {
		this.username = username;
		this.password = password;
		this.nickname = nickname;
		this.email = email;
		this.phone = phone;
		this.phoneHash = phoneHash;
	}

	public boolean isSystem() {
		return "system".equals(username);
	}
}
