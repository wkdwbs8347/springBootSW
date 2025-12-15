package com.example.demo.dto;

import lombok.Data;

//메시지 전달용 DTO
@Data
public class BuildingChatMessage {
 private Long roomId;
 private Long userId;
 private String content;
 private String nickname; // 브로드캐스트 시 포함
 private String sentDate;
 private String profileImage;
}
