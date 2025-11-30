package com.example.demo.dao;

import com.example.demo.dto.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserDao {

    // 아이디 중복 체크
    @Select("SELECT COUNT(*) FROM `user` WHERE loginId = #{loginId}")
    int countByLoginId(@Param("loginId") String loginId);

    // 닉네임 중복 체크
    @Select("SELECT COUNT(*) FROM `user` WHERE nickname = #{nickname}")
    int countByNickname(@Param("nickname") String nickname);
    
    // 이메일 중복 체크
    @Select("SELECT COUNT(*) FROM `user` WHERE email = #{email}")
    int countByEmail(String email);

    // 회원가입 저장
    @Insert("""
        INSERT INTO `user`
        (loginId, loginPw, nickname, email, userName, birth)
        VALUES
        (#{loginId}, #{loginPw}, #{nickname}, #{email}, #{userName}, #{birth})
        """)
    void insertUser(User user);

	
}