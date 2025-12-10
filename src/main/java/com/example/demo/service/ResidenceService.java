package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * 입주 신청 처리
     * 1. residence 테이블에 신청 저장
     * 2. 건물 owner에게 알림 생성
     */
    @Transactional
    public void moveIn(MoveInRequest req) {
        residenceDao.insertMoveIn(req);

        // owner 조회 후 알림 생성
        Integer ownerId = residenceDao.findBuildingOwner(req.getBuildingId());
        if (ownerId != null) {
            String msg = "입주 신청이 있습니다."; // 알림 메시지
            String link = "/owner/apply-list?buildingId=" + req.getBuildingId(); // 클릭 시 이동
            notificationDao.insertNotification(ownerId, msg, link);
        }
    }

    // 입주 승인
    @Transactional
    public void approve(int id) {
    	residenceDao.updateStatus(id, "checked");
        Residence res = residenceDao.detail(id);
        // 건물 멤버 등록
        residenceDao.insertBuildingMember(res.getBuildingId(), res.getUserId());
        // unit 현재 입주자 업데이트
        residenceDao.updateCurrentResident(res.getUnitId(), res.getUserId());
    }

    // 입주 거절
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
}
