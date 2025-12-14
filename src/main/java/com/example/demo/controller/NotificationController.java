package com.example.demo.controller;

import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.Notification;
import com.example.demo.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate; // WebSocket 전송

    public NotificationController(NotificationService notificationService,
                                  SimpMessagingTemplate messagingTemplate) {
        this.notificationService = notificationService;
        this.messagingTemplate = messagingTemplate;
    }

    // 알림 생성 + WebSocket 실시간 전송
    @PostMapping("/send")
    public void sendNotification(@RequestBody Notification notification) {
        // 1) DB 저장
        notification.setId(null);  // id를 명시적으로 null로 설정
        notificationService.createNotification(notification);
        System.out.println("생성된 알림 ID: " + notification.getId());  // 이 값을 확인

        // 2) WebSocket 전송 (실시간 알림)
        messagingTemplate.convertAndSend(
            "/topic/notifications/" + notification.getUserId(),
            notification
        );
    }

    // 미확인 알림 조회
    @GetMapping("/{userId}")
    public List<Notification> getNotifications(@PathVariable Integer userId) {
        // 미확인 알림만 가져옴
        return notificationService.getUnreadNotifications(userId);
    }

    // 읽음 처리 (특정 알림)
    @PutMapping("/mark-read/{id}")
    public void markAsRead(@PathVariable Integer id) {
    	System.out.println(id);
        notificationService.markAsRead(id);
    }

    // 모든 알림 읽음 처리
    @PutMapping("/mark-all-read/{userId}")
    public void markAllAsRead(@PathVariable Integer userId) {
        notificationService.markAllNotificationsAsRead(userId);
    }
}