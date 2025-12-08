package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 단위 호수(Unit) DTO
 * 각 층과 호수를 담아 batch insert 용
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Unit {
	private int buildingId;
	private int floor;
	private String unitNumber;
}
