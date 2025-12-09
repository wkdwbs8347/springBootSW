package com.example.demo.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 입주 신청 정보 DTO
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Residence {
    private int id;               // 신청 고유 ID
    private int userId;           // 신청자 user.id
    private String nickname;      // 신청자 닉네임
    private int buildingId;       // 신청한 건물 ID
    private int floor;            // 신청 층수
    private int unitId;           // 신청 호수 unit.id
    private String unitNumber;    // 호수 번호
    private String status;        // 상태(waiting/checked)
    private LocalDateTime requestDate; // 신청일
}