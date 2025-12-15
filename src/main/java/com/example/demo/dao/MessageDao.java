package com.example.demo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.example.demo.dto.Message;

@Mapper
public interface MessageDao {
	
	// 메세지 추가
	@Insert("""
			INSERT INTO message (senderId, receiverId, title, content)
			    VALUES (#{senderId}, #{receiverId}, #{title}, #{content})
			""")
	@Options(useGeneratedKeys = true, keyProperty = "id") // DTO(Message)의 id 필드에 DB에서 생성된 키 값을 채워 넣으라는 의미
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
    
 // 여러 메시지 삭제
    @Delete("""
    	<script>
    	    DELETE FROM message
    		    WHERE id IN
    		    <foreach item="id" collection="ids" open="(" separator="," close=")">
    		        #{id}
    		    </foreach>
        </script>
    """)
    void deleteMessages(@Param("ids") List<Long> ids);
    
    // 메세지 읽음 처리
    @Update("UPDATE message SET isRead = 1 WHERE id = #{id} AND isRead = 0")
    void updateIsRead(@Param("id") Long id);
}
