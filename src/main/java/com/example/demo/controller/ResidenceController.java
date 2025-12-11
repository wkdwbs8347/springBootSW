package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.MoveInRequest;
import com.example.demo.dto.Notification;
import com.example.demo.dto.Residence;
import com.example.demo.service.ResidenceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/residence")
public class ResidenceController {

    private final ResidenceService residenceService;

    /** 멤버 신청 */
    @PostMapping("/move-in")
    public ResponseEntity<?> moveIn(@RequestBody MoveInRequest req) {
        residenceService.moveIn(req);
        return ResponseEntity.ok(Map.of("success", true));
    }

    /** 신청 승인 */
    @PutMapping("/approve/{id}")
    public ResponseEntity<?> approve(@PathVariable int id) {
        residenceService.approve(id);
        return ResponseEntity.ok(Map.of("success", true));
    }

    /** 신청 거절 */
    @DeleteMapping("/reject/{id}")
    public ResponseEntity<?> reject(@PathVariable int id) {
        residenceService.reject(id);
        return ResponseEntity.ok(Map.of("success", true));
    }

    /** 신청 상세 조회 */
    @GetMapping("/detail/{id}")
    public Residence detail(@PathVariable int id) {
        return residenceService.detail(id);
    }

    /** 건물별 신청 목록 */
    @GetMapping("/apply-list")
    public List<Residence> applyList(@RequestParam int buildingId) {
        return residenceService.listApply(buildingId);
    }

    /** 미확인 알림 목록 조회 */
    @GetMapping("/notifications/{userId}")
    public List<Notification> getUnreadNotifications(@PathVariable int userId) {
        return residenceService.listUnreadNotifications(userId);
    }

    /** 알림 확인 처리 */
    @PutMapping("/notifications/mark-read/{id}")
    public ResponseEntity<?> markNotificationRead(@PathVariable int id) {
        residenceService.markNotificationRead(id);
        return ResponseEntity.ok(Map.of("success", true));
    }
}