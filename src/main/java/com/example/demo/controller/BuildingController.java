package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.BuildingRegister;
import com.example.demo.dto.Unit;
import com.example.demo.service.BuildingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/building")
@RequiredArgsConstructor
public class BuildingController {

	private final BuildingService buildingService;
	
	/**
	 * 프론트에서 POST 요청으로 건물 등록 예: { createdUsr: 1, name: "빌딩A", address: "서울",
	 * totalFloor: 5, room: 4 }
	 */
	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody BuildingRegister payload) {
	    try {
	        buildingService.registerBuilding(payload);
	        return ResponseEntity.ok().body(Map.of("success", true, "message", "건물 등록 완료!"));
	    } catch (RuntimeException e) {
	        // 중복 건물 메시지 전달
	        return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
	    } 
	}
	
    /**
     * 주소별 건물 조회
     */
    @GetMapping("/byAddress")
    public List<BuildingRegister> getBuildingsByAddress(@RequestParam String address) {
        return buildingService.getBuildingsByAddress(address);
    }

    /**
     * 건물 ID별 층/호수 조회
     */
    @GetMapping("/floor-unit")
    public List<Unit> getFloorUnits(@RequestParam int buildingId) {
        return buildingService.getUnitsByBuilding(buildingId);
    }
}