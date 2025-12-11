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
    private int id;           // DB PK
    private int buildingId;   // 건물 ID
    private int floor;        // 층
    private int unitNumber; // 호수 ex) 101
    private Integer currentResidentId; // 현재 입주자 (nullable)
}
