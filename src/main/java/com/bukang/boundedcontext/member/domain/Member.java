package com.bukang.boundedcontext.member.domain;

import com.bukang.shared.member.domain.SourceMember;
import com.bukang.shared.member.dto.MemberDto;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "MEMBER_MEMBER")
public class Member extends SourceMember {
	public Member(
		String username,
		String email,
		String password,
		String nickname,
		String phone,
		String phoneHash
	) {
		super(username, password, nickname, email, phone, phoneHash);
	}

	public MemberDto toDto() {
		return new MemberDto(
			getId(),
			getCreateDate(),
			getModifyDate(),
			getUsername(),
			getNickname(),
			getEmail()
		);
	}
}
