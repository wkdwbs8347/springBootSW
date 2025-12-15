package com.example.demo.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.example.demo.dto.BuildingChatMessage;
import com.example.demo.service.BuildingChatMsgService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class BuildingChatController {

    private final SimpMessagingTemplate template;
    private final BuildingChatMsgService chatMsgService;

    @MessageMapping("/building/{roomId}")
    public void sendMessage(@DestinationVariable Long roomId, BuildingChatMessage msg) {
        BuildingChatMessage savedMsg = chatMsgService.saveMessage(roomId, msg);
        template.convertAndSend("/topic/building/" + roomId, savedMsg);
    }
}