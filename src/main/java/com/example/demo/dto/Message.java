package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private Long id; // 메세지 고유 식별자
    private int senderId; // 보낸사람 고유 식별자
    private int receiverId;  // 받는사람 고유 식별자
    private int floor; // unit테이블의 보낸사람 층
    private int unitNumber; // unit테이블의 보낸사람 호수
    private String title; // 제목
    private String content; // 내용
    private String sentDate; // 보낸날짜
    private int isRead; // 읽음여부
    private String senderName; // 보낸사람 닉네임
}
