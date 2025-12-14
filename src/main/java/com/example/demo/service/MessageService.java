package com.example.demo.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.example.demo.dao.MessageDao;
import com.example.demo.dao.UserDao;
import com.example.demo.dto.Message;
import com.example.demo.dto.Notification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageDao messageDao;
    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserDao userDao;  

    /**
     * 메시지 전송 (DB + 실시간 + 알림)
     */
    public void sendMessage(Message message) {
        // 보낸 사람과 받는 사람이 같으면 예외를 발생시킨다
        if (message.getSenderId() == message.getReceiverId()) {
            throw new IllegalArgumentException("본인이 본인에게 메시지를 보낼 수 없습니다.");
        }
        
        // 메시지 보내는 사람의 닉네임을 조회하여 실시간 메시지에 포함
        String senderName = userDao.getNicknameById(message.getSenderId());
        message.setSenderName(senderName);
        
        // 실시간 메시지 전송
        messagingTemplate.convertAndSend(
        		"/topic/messages/" + message.getReceiverId(), 
        		message
        		);
        
        // 메시지 DB 저장
        messageDao.insertMessage(message);
        

        // 알림 생성
        Notification notification = new Notification();
        notification.setUserId(message.getReceiverId());
        notification.setMessage("새 메시지가 도착했습니다.");
        notification.setLink("/mypage/message");

        notificationService.createNotification(notification);

        // 실시간 알림 전송
        messagingTemplate.convertAndSend(
            "/topic/notifications/" + message.getReceiverId(),
            notification
        );
    }

    // 메시지 목록
    public List<Message> getMessages(int receiverId) {
        return messageDao.getMessagesByReceiver(receiverId);
    }
    
    // 메시지 상세보기
    public Message getMessageById(Long id) {
        return messageDao.getMessageById(id);
    }

    // 메시지 삭제
    public void deleteMessage(Long id) {
        messageDao.deleteMessage(id);
    }
}