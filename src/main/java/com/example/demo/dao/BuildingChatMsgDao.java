package com.example.demo.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

@Mapper
public interface BuildingChatMsgDao {

    @Insert("INSERT INTO buildingChatMsg (roomId, userId, content, sentDate) " +
            "VALUES (#{roomId}, #{userId}, #{content}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertMessage(Long roomId, Long userId, String content);
}