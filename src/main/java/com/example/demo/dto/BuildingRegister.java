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
public class BuildingRegister {
	private String name; // building.name
    private String address; // building.address
    private int totalFloor; // building.total_floor
    private int room; // unit.unitNumber
    private int createdUsr; // building.created_usr
    private int id;   // insert 후 MyBatis에서 자동 생성키 반환용
}
