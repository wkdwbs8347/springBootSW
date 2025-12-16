package com.example.demo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.example.demo.dto.BuildingChatMessage;

@Mapper
public interface BuildingChatMsgDao {

	@Insert("INSERT INTO buildingChatMsg (roomId, userId, content, sentDate) "
			+ "VALUES (#{roomId}, #{userId}, #{content}, NOW())")
	@Options(useGeneratedKeys = true, keyProperty = "id")
	void insertMessage(BuildingChatMessage msg);

	@Delete("DELETE FROM buildingChatMsg WHERE id = #{messageId}")
	void deleteMessage(Long messageId);

	@Update("UPDATE buildingChatMsg SET content = #{content} WHERE id = #{messageId}")
	void updateMessageContent(Long messageId, String content);

	@Select("SELECT * FROM buildingChatMsg WHERE id = #{messageId}")
	BuildingChatMessage getMessageById(Long messageId);

	@Select("""
	   		SELECT bcm.*, u.nickname, u.profileImage
				FROM buildingChatMsg AS bcm
				JOIN `user` AS u
				ON u.id = bcm.userId
				WHERE roomId = #{roomId} ORDER BY sentDate ASC;
			   		""")
	List<BuildingChatMessage> getMessagesByRoomId(Long roomId);

}