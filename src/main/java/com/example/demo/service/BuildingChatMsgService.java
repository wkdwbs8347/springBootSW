package com.example.demo.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.example.demo.dao.BuildingChatMsgDao;
import com.example.demo.dao.UserDao;
import com.example.demo.dto.BuildingChatMessage;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BuildingChatMsgService {

    private final BuildingChatMsgDao chatMsgDao;
    private final UserDao userDao; // nickname, profile 가져오기

    public BuildingChatMessage saveMessage(Long roomId, BuildingChatMessage msg) {
        // DB 저장
        chatMsgDao.insertMessage(roomId, msg.getUserId(), msg.getContent());

        // nickname 가져오기
        String nickname = userDao.getNicknameById(msg.getUserId());
        // 프로필 이미지 가져오기
        String profileImage = userDao.getProfileImageById(msg.getUserId());

        msg.setNickname(nickname);
        msg.setProfileImage(profileImage);
        msg.setSentDate(LocalDateTime.now().toString());

        return msg; // 브로드캐스트용 DTO
    }
}