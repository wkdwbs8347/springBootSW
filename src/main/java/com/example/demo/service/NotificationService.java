package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dao.NotificationDao;
import com.example.demo.dto.Notification;

@Service
@Transactional
public class NotificationService {

    private final NotificationDao notificationDao;

    public NotificationService(NotificationDao notificationDao) {
        this.notificationDao = notificationDao;
    }

    // 알림 생성
    public void createNotification(Notification notification) {
        notificationDao.insertNotification(notification);
    }

    // 미확인 알림 조회
    public List<Notification> getUnreadNotifications(Integer userId) {
        return notificationDao.listUnread(userId);
    }

    // 알림 읽음 처리
    public void markAsRead(Integer id) {
        notificationDao.markAsRead(id);
    }

    // 모든 알림 읽음 처리
    public void markAllNotificationsAsRead(Integer userId) {
        notificationDao.markAllNotificationsAsRead(userId);
    }
}