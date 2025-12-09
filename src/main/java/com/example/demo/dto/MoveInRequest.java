package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 입주 신청 요청 DTO
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MoveInRequest {
    private int userId;       // 신청자 ID
    private int buildingId;   // 신청 건물 ID
    private int unitId;       // 신청 호수 ID
    private int floor;        // 신청 층수
}