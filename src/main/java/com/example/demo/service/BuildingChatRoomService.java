package com.example.demo.service;

import org.springframework.stereotype.Service;

import com.example.demo.dao.BuildingChatRoomDao;
import com.example.demo.dto.BuildingChatRoom;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BuildingChatRoomService {

    private final BuildingChatRoomDao buildingChatRoomDao;

    // 건물별 채팅방 조회
    public Long getRoomIdByBuildingId(int buildingId) {
        return buildingChatRoomDao.findRoomIdByBuildingId(buildingId);
    }

    // 채팅방 생성
    public void createRoom(int buildingId) {
    	 BuildingChatRoom room = new BuildingChatRoom();
         room.setBuildingId(buildingId);

         // DB Insert 후 room.id에 자동 생성키 값 들어감
         buildingChatRoomDao.insertRoom(room);

         System.out.println("채팅방 생성 완료! roomId: " + room.getId());
    }
}