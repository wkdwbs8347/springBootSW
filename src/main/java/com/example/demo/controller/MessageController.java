package com.example.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.Message;
import com.example.demo.service.MessageService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(
            @RequestBody Message message,
            HttpSession session
    ) {
    	
        Integer senderId = (Integer) session.getAttribute("userId");
        
        // 본인이 본인에게 메시지를 보내는 경우를 차단
        if (senderId.equals(message.getReceiverId())) {
            return ResponseEntity.badRequest().body("본인에게 메시지를 보낼 수 없습니다.");  // 또는 Error 메시지 반환
        }
        message.setSenderId(senderId);

        messageService.sendMessage(message);

        return ResponseEntity.ok().build();
    }

    // 메시지 목록 조회
    @GetMapping("/{receiverId}")
    public List<Message> getMessages(@PathVariable int receiverId) {
        return messageService.getMessages(receiverId);
    }
    
    // 메시지 상세 조회
    @GetMapping("/detail/{id}")
    public ResponseEntity<Message> getMessageDetail(@PathVariable Long id) {
        Message message = messageService.getMessageById(id);
        if (message == null) {
            return ResponseEntity.notFound().build(); // 메시지 없음
        }
        return ResponseEntity.ok(message);
    }

    // 메시지 삭제
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {
        messageService.deleteMessage(id);
        return ResponseEntity.ok().build();
    }
    
    // 여러 메시지 삭제
    @DeleteMapping("/delete/batch")
    public ResponseEntity<Void> deleteMessages(
            @RequestBody List<Long> messageIds,
            HttpSession session
    ) {
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        messageService.deleteMessages(messageIds, userId);
        return ResponseEntity.ok().build();
    }
    
    // 메세지 읽음 처리
    @PatchMapping("/read/{id}")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");

        Message message = messageService.getMessageById(id);

        if (userId == null || message.getReceiverId() != userId) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        messageService.markAsRead(id);

        return ResponseEntity.ok().build();
    }
}