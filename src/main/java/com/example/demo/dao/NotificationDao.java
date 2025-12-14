package com.example.demo.dao;

import java.util.List;

import org.apache.ibatis.annotations.*;

import com.example.demo.dto.Notification;

@Mapper
public interface NotificationDao {

    // 알림 추가 (id 자동 생성 후 객체에 할당)
    @Insert("INSERT INTO notification(userId, message, link) VALUES(#{userId}, #{message}, #{link})")
    @Options(useGeneratedKeys = true, keyProperty = "id")  // id를 자동으로 채워줌
    void insertNotification(Notification notification);

    // 미확인 알림만 조회
    @Select("SELECT * FROM notification WHERE userId = #{userId} AND isRead = 0 ORDER BY regDate DESC")
    List<Notification> listUnread(@Param("userId") Integer userId);

    // 알림 읽음 처리
    @Update("UPDATE notification SET isRead = 1 WHERE id = #{id} AND isRead = 0")
    void markAsRead(@Param("id") Integer id);

    // 모든 알림 읽음 처리
    @Update("UPDATE notification SET isRead = 1 WHERE userId = #{userId} AND isRead = 0")
    void markAllNotificationsAsRead(@Param("userId") Integer userId);
}