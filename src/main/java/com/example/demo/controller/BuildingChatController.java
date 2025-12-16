package com.example.demo.controller;

import java.util.List;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.BuildingChatMessage;
import com.example.demo.service.BuildingChatMsgService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class BuildingChatController {

	private final SimpMessagingTemplate template;
	private final BuildingChatMsgService chatMsgService;

	@MessageMapping("/building/{roomId}")
	public void sendMessage(@DestinationVariable Long roomId, BuildingChatMessage msg) {
		BuildingChatMessage savedMsg = chatMsgService.saveMessage(roomId, msg);
		template.convertAndSend("/topic/building/" + roomId, savedMsg);
	}

	@DeleteMapping("/building/chat/{messageId}")
	public void deleteMessage(@PathVariable Long messageId) {
		chatMsgService.deleteMessage(messageId);
	}

	@PutMapping("/building/chat/{messageId}")
	public BuildingChatMessage updateMessage(@PathVariable Long messageId, @RequestBody String content) {
		return chatMsgService.updateMessage(messageId, content);
	}

	@MessageMapping("/building/chat/delete")
	public void sendDeleteMessage(Long messageId) {
		template.convertAndSend("/topic/building/delete", messageId);
	}

	@MessageMapping("/building/chat/update")
	public void sendUpdateMessage(BuildingChatMessage msg) {
		template.convertAndSend("/topic/building/update", msg);
	}
	
    // 특정 방 메시지 전체 조회
    @GetMapping("/building/chat/room/{roomId}")
    public List<BuildingChatMessage> getMessagesByRoom(@PathVariable Long roomId) {
        return chatMsgService.getMessagesByRoomId(roomId);
    }
	

}