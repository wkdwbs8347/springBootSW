package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BuildingMember {
    private int userId; 		// 유저 식별자
    private String nickname; 	// 유저 닉네임
    private String role; 		// owner, resident
    private Integer unitId; // 호 고유 식별
    private Integer unitNumber; // 호
    private Integer floor;      // 층
    private String profileImage; // 유저 프로필 이미지
    private String joinedAt; // 등록일
}
 