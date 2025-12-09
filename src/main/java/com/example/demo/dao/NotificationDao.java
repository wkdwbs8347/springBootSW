package com.example.demo.dao;

import java.util.List;

import com.example.demo.dto.Notification;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface NotificationDao {

    // 알림 추가 (입주 신청 발생 시 owner에게 발송)
    @Insert("""
        INSERT INTO notification(userId, message, link, isRead, regDate)
        VALUES(#{userId}, #{message}, #{link}, 0, NOW())
    """)
    void insertNotification(@Param("userId") int userId,
                            @Param("message") String message,
                            @Param("link") String link);

    // 미확인 알림 리스트 조회
    @Select("SELECT * FROM notification WHERE userId=#{userId} AND isRead=0 ORDER BY regDate DESC")
    List<Notification> listUnread(@Param("userId") int userId);

    // 알림 읽음 처리
    @Update("UPDATE notification SET isRead=1 WHERE id=#{id}")
    void markAsRead(@Param("id") int id);
}
