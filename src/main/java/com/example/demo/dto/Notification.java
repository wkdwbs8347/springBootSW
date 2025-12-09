package com.example.demo.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 알림 정보 DTO
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    private int id;               // 알림 고유 ID
    private int userId;           // 알림 받는 user.id
    private String message;       // 알림 메시지
    private String link;          // 클릭 시 이동할 링크
    private boolean isRead;       // 읽음 여부
    private LocalDateTime regDate;// 알림 생성일
}