package com.example.demo.dto;

import lombok.Data;

@Data
public class BuildingChatRoom {
    private Integer id;          // PK, MyBatis에서 자동 생성
    private String regDate;  // 생성일
    private int buildingId;  // FK, 연결할 건물 ID
}
