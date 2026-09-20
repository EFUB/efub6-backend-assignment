package com.example.community.auth;

import com.example.community.member.domain.MemberStatus;

public record AuthenticatedMember(Long memberId, String email, String nickname, MemberStatus status) {}
