package com.example.demo.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.example.demo.dto.BuildingChatRoom;

@Mapper
public interface BuildingChatRoomDao {
	
	// 건물 등록시 채팅방 바로 생성 
    @Insert("INSERT INTO buildingChatRoom (buildingId) VALUES (#{buildingId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
	void insertRoom(BuildingChatRoom  room);
    
    // 건물별 채팅방 id(번호)조회
    @Select("SELECT id FROM buildingChatRoom WHERE buildingId = #{buildingId}")
    Long findRoomIdByBuildingId(@Param("buildingId") int buildingId);
}