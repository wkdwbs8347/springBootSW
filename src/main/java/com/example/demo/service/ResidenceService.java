package com.example.demo.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dao.NotificationDao;
import com.example.demo.dao.ResidenceDao;
import com.example.demo.dto.MoveInRequest;
import com.example.demo.dto.Notification;
import com.example.demo.dto.Residence;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ResidenceService {

    private final ResidenceDao residenceDao;
    private final NotificationDao notificationDao;
    private final SimpMessagingTemplate messagingTemplate; // WebSocket 주입

    /**
     * 멤버 신청 처리
     * 1. residence 테이블에 신청 저장
     * 2. 건물 owner에게 알림 생성 + WebSocket 실시간 전송
     */
    @Transactional
    public void moveIn(MoveInRequest req) {
        // 1) DB 저장
        residenceDao.insertMoveIn(req);

        // 2) owner 조회 후 알림 생성
        Integer ownerId = residenceDao.findBuildingOwner(req.getBuildingId());
        if (ownerId != null) {
            String msg = "입주 신청이 있습니다."; // 알림 메시지
            String link = "/mypage/building/apply-list?buildingId=" + req.getBuildingId(); // 클릭 시 /mypage/building/apply-list로 이동

            // DB 저장
            Notification notification = new Notification();
            notification.setUserId(ownerId);
            notification.setMessage(msg);
            notification.setLink(link);
            notificationDao.insertNotification(notification);

            // WebSocket 실시간 전송
            messagingTemplate.convertAndSend(
                "/topic/notifications/" + ownerId,
                notification
            );
        }
    }

    // 멤버신청 승인
    @Transactional
    public void approve(int id) {
        residenceDao.updateStatus(id, "checked");
        Residence res = residenceDao.detail(id);

        String role = residenceDao.findMemberRole(res.getBuildingId(), res.getUserId());

        if ("owner".equals(role)) {
            residenceDao.updateOwnerUnit(res.getBuildingId(), res.getUserId(), res.getUnitId());
        } else {
            residenceDao.insertBuildingMember(res.getBuildingId(), res.getUserId(), res.getUnitId());
        }

        residenceDao.updateCurrentResident(res.getUnitId(), res.getUserId());
    }

    // 멤버신청 거절
    @Transactional
    public void reject(int id) {
        residenceDao.deleteMoveIn(id);
    }

    // 신청 상세 조회
    public Residence detail(int id) {
        return residenceDao.detail(id);
    }

    // 건물별 신청 목록
    public List<Residence> listApply(int buildingId) {
        return residenceDao.listApply(buildingId);
    }

    // 미확인 알림 목록
    public List<Notification> listUnreadNotifications(int userId) {
        return notificationDao.listUnread(userId);
    }

    // 알림 읽음 처리
    public void markNotificationRead(int id) {
        notificationDao.markAsRead(id);
    }

    // 이미지 프로젝트 폴더에 저장
    public String uploadMoveInImage(MultipartFile file, String oldImageUrl) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("파일이 비어 있습니다.");
        }

        String uploadDir = "uploads";

        // 기존 이미지 삭제
        if (oldImageUrl != null && !oldImageUrl.isBlank()) {
            String filename = oldImageUrl.substring(oldImageUrl.lastIndexOf("/") + 1);
            Path oldPath = Paths.get(uploadDir, filename);

            if (Files.exists(oldPath)) {
                Files.delete(oldPath);
            }
        }

        // 새 이미지 저장
        if (!Files.exists(Paths.get(uploadDir))) {
            Files.createDirectories(Paths.get(uploadDir));
        }

        String original = file.getOriginalFilename();
        String ext = "";

        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf("."));
        }

        String filename = System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8) + ext;

        Path newPath = Paths.get(uploadDir, filename);
        Files.copy(file.getInputStream(), newPath, StandardCopyOption.REPLACE_EXISTING);

        return "http://localhost:8080/uploads/" + filename;
    }
}