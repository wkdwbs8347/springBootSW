package com.example.demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
		if (userId == null) {
			return ResponseEntity.status(403).body("조회 권한이 없습니다.");
		}
		List<Building> buildings = buildingService.getBuildingsByOwner(userId);
		return ResponseEntity.ok(buildings);
	}

	// resident 리스트 페이지
	@GetMapping("/byResident")
	public ResponseEntity<?> getBuildingsByResident(HttpSession session) {
		Integer userId = (Integer) session.getAttribute("userId");
		if (userId == null) {
			return ResponseEntity.status(401).body("조회 권한이 없습니다.");
		}
		List<Building> buildings = buildingService.getBuildingsByResident(userId);
		return ResponseEntity.ok(buildings);
	}

	// 건물 상세 조회
	@GetMapping("/detail")
	public ResponseEntity<?> getBuildingDetail(@RequestParam Integer buildingId,
	                                           @RequestParam(required = false) Integer unitId,
	                                           HttpSession session) {
		
	    Integer userId = (Integer) session.getAttribute("userId");
	    
	    if (userId == null) {
	        return ResponseEntity.status(401).body("로그인이 필요합니다");
	    }

	    // DB 기준으로 Owner 여부 확인
	    boolean isOwner = buildingService.isOwnerOfBuilding(userId, buildingId);
	    System.out.println(isOwner);
	    Building building = buildingService.getBuildingDetail(userId, buildingId, unitId, isOwner);

	    if (building == null)
	        return ResponseEntity.status(403).body("조회 권한이 없습니다.");

	    Map<String, Object> result = new HashMap<>();
	    result.put("building", building);
	    result.put("isOwner", isOwner); // 프론트에서 사용할 수 있도록 반환

	    return ResponseEntity.ok(result);
	}
	
	   // 이미지 업로드 (신규/임시)
    @PostMapping("/upload-image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String url = buildingService.uploadBuildingImage(file);
            return ResponseEntity.ok(Map.of("buildingImage", url));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
    
    // 기존 이미지 삭제 후 새 이미지 업로드
    @PutMapping("/update-image")
    public ResponseEntity<?> updateImage(@RequestParam int buildingId,
                                         @RequestParam("file") MultipartFile file) {
        try {
            String newImageUrl = buildingService.updateBuildingImage(buildingId, file);
            return ResponseEntity.ok(Map.of("buildingImage", newImageUrl));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

}
