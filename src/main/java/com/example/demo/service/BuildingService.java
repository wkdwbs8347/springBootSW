package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dao.BuildingDao;
import com.example.demo.dto.BuildingRegister;
import com.example.demo.dto.Unit;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BuildingService {

    private final BuildingDao buildingDao;

    /**
     * 1️⃣ 건물 등록 후 2️⃣ 층별 단위 호수 자동 생성
     */
    @Transactional
    public void registerBuilding(BuildingRegister dto) {

        // 중복 체크
        int exists = buildingDao.countByNameAndAddress(dto.getName(), dto.getAddress());
        if (exists > 0) {
            throw new RuntimeException("이미 등록된 건물입니다.");
        }

        // 건물 등록
        buildingDao.insertBuilding(dto);
        int buildingId = dto.getId();

        // 층별 호수 생성
        List<Unit> units = new ArrayList<>();
        for (int floor = 1; floor <= dto.getTotalFloor(); floor++) {
            for (int i = 1; i <= dto.getRoom(); i++) {
                Unit unit = new Unit();
                unit.setBuildingId(buildingId);
                unit.setFloor(floor);
                unit.setUnitNumber(floor + String.format("%02d", i));
                units.add(unit);
            }
        }
        buildingDao.insertUnits(units);
    }
    
    /**
     * 주소로 건물 목록 조회
     */
    public List<BuildingRegister> getBuildingsByAddress(String address) {
        return buildingDao.selectByAddress(address);
    }

    /**
     * 건물 ID로 층/호수 목록 조회
     */
    public List<Unit> getUnitsByBuilding(int buildingId) {
        return buildingDao.selectUnitsByBuilding(buildingId);
    }
    
    // owner 리스트 페이지
    public List<BuildingRegister> getBuildingsByOwner(Integer userId) {
        return buildingDao.selectByOwnerList(userId);
    }
    
    // resident 리스트 페이지
    public List<BuildingRegister> getBuildingsByResident(Integer userId) {
        return buildingDao.selectByResidentList(userId);
    }
    
    
    // owner , resident 건물 상세 조회 분기처리
    public BuildingRegister getBuildingDetail(int buildingId, int userId, boolean isOwner) {
        if (isOwner) {
            return buildingDao.selectByOwner(userId, buildingId);
        } else {
            return buildingDao.selectByResident(userId, buildingId);
        }
    }
}