package com.example.demo.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.BuildingRegister;
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
}