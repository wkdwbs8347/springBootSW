package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 건물 등록 요청 DTO
 * 프론트에서 보내는 payload와 컬럼명을 맞춤
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Building {
	private int id;   // insert 후 MyBatis에서 자동 생성키 반환용
	private int createdUserId; // building.created_usr
	private String nickname;
	private String name; // building.name
    private String address; // building.address
    private String regDate;
    private int totalFloor; // building.total_floor
    private int floor; // 층
    private int unitNumber; // unit.unitNumber
    private int unitCnt; // 세대 수
    private int unitId;  // unit.id
    private String profileImage; // 건물 이미지
}
