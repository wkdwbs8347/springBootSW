package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.Building;
import com.example.demo.dto.Unit;
import com.example.demo.service.BuildingService;

import jakarta.servlet.http.HttpSession;
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
	public ResponseEntity<?> register(@RequestBody Building payload) {
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
    public List<Building> getBuildingsByAddress(@RequestParam String address) {
        return buildingService.getBuildingsByAddress(address);
    }

    /**
     * 건물 ID별 층/호수 조회
     */
    @GetMapping("/floor-unit")
    public List<Unit> getFloorUnits(@RequestParam int buildingId) {
        return buildingService.getUnitsByBuilding(buildingId);
    }
    
    // owner 리스트 페이지
    @GetMapping("/byOwner")
    public ResponseEntity<?> getBuildingsByOwner(HttpSession session) {
    	Integer userId = (Integer) session.getAttribute("userId");
    	Boolean isOwner = (Boolean) session.getAttribute("isOwner");
    	if (userId == null || isOwner == null || !isOwner) {
    		return ResponseEntity.status(403).body("조회 권한이 없습니다.");
    	}
    	List<Building> buildings = buildingService.getBuildingsByOwner(userId);
    	return ResponseEntity.ok(buildings);
    }
    
    // resident 리스트 페이지
    @GetMapping("/byResident")
    public ResponseEntity<?> getBuildingsByResident(HttpSession session) {
    	Integer userId = (Integer) session.getAttribute("userId");
    	if (userId == null) return ResponseEntity.status(401).body("로그인이 필요합니다.");
    	List<Building> buildings = buildingService.getBuildingsByResident(userId);
    	return ResponseEntity.ok(buildings);
    }
    
    // 건물 상세 조회 (ID)
    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getBuildingDetail(@PathVariable int id, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId"); // 로그인 유저 id
        Boolean isOwner = (Boolean) session.getAttribute("isOwner"); // Owner 여부 (세션에 저장)
        
        if (userId == null) return ResponseEntity.status(401).body("로그인이 필요합니다");

        Building building = buildingService.getBuildingDetail(id, userId, isOwner != null && isOwner);
        if (building == null) return ResponseEntity.status(403).body("조회 권한이 없습니다.");

        return ResponseEntity.ok(building);
    }
    
}
