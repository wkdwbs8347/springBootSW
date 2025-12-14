package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.dao.BuildingMemberDao;
import com.example.demo.dto.BuildingMember;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BuildingMemberService {
	private final BuildingMemberDao buildingMemberDao;
	
	// 멤버 리스트
    public List<BuildingMember> getMembersByBuildingId(int buildingId) {
        return buildingMemberDao.selectMembersByBuildingId(buildingId);
    }
    
    // 멤버 상세 조회
    public BuildingMember getMemberByUserIdAndUnitId(int id, int userId, int unitId) {
        return buildingMemberDao.selectMemberByUserIdAndUnitId(id, userId, unitId);
    }
    
}
