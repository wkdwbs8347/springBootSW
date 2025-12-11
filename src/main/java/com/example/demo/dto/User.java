package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
	private int id; // pk 
    private String loginId; // 로그인Id
    private String loginPw; // 로그인Pw
    private String nickname; // 닉네임
    private String email; // 이메일
    private String userName; // 이름
    private String birth; // 생일
    private String regDate; // 가입일
    private String profileImage; // 프로필 이미지
}