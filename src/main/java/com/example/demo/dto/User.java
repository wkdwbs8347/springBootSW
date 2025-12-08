package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
	private int id;
    private String loginId;
    private String loginPw;
    private String nickname;
    private String email;
    private String userName;
    private String birth;
    private String regDate;
}