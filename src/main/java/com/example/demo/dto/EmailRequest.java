package com.example.demo.dto;

import lombok.Data;

@Data
public class EmailRequest {
    private String email;
    private String code; // 인증번호 확인용
}
