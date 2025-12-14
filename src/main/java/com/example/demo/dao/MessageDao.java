package com.example.demo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.example.demo.dto.Message;

@Mapper
public interface MessageDao {
	
	// 메세지 추가
	@Insert("""
			INSERT INTO message (senderId, receiverId, title, content)
			    VALUES (#{senderId}, #{receiverId}, #{title}, #{content})
			""")
	void insertMessage(Message message);
	
	// 메세지 리스트
	@Select("""
			SELECT m.id, m.senderId, m.receiverId, m.title, m.content, m.sentDate, m.isRead, u.nickname AS senderName
			  FROM message m
			  JOIN user u ON m.senderId = u.id
			  WHERE m.receiverId = #{receiverId}
			  ORDER BY m.sentDate DESC
			""")
	List<Message> getMessagesByReceiver(@Param("receiverId") int receiverId);
	
    // 메시지 상세보기
    @Select("""
            SELECT m.id, m.senderId, m.receiverId, m.title, m.content, m.sentDate, m.isRead, u.nickname AS senderName,
                   un.floor, un.unitNumber
            FROM message m
            JOIN `user` u ON m.senderId = u.id
            JOIN unit un ON un.currentResidentId = m.senderId
            WHERE m.id = #{id}
            """)
    Message getMessageById(@Param("id") Long id);

    // 메시지 삭제
    @Delete("""
            DELETE FROM message WHERE id = #{id}
            """)
    void deleteMessage(@Param("id") Long id);
}
