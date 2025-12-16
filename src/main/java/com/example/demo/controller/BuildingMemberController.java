package com.example.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.BuildingMember;
import com.example.demo.service.BuildingMemberService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/buildingMember")
public class BuildingMemberController {

	private final BuildingMemberService buildingMemberService;
	// 멤버 리스트
	@GetMapping("/list")
	public List<BuildingMember> getMemberList(@RequestParam int buildingId) {
		return buildingMemberService.getMembersByBuildingId(buildingId);
	}
	
    // 멤버 상세 조회
    @GetMapping("/detail")
    public BuildingMember getMemberDetail(
    		@RequestParam int id,
            @RequestParam int userId,
            @RequestParam(required=false) Integer unitId
            ) {
        return buildingMemberService.getMemberByUserIdAndUnitId(id, userId, unitId);
    }
    
    // Owner 전용 상세 조회
    @GetMapping("/ownerDetail")
    public BuildingMember getOwnerDetail(
            @RequestParam int id,       // buildingId
            @RequestParam int userId
    ) {
        return buildingMemberService.getOwnerByUserId(id, userId);
    }

}
