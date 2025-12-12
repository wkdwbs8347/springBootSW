package com.example.demo.dao;

import com.example.demo.dto.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserDao {

	// 아이디 중복 체크
	@Select("SELECT COUNT(*) FROM `user` WHERE loginId = #{loginId}")
	int countByLoginId(@Param("loginId") String loginId);

	// 닉네임 중복 체크
	@Select("SELECT COUNT(*) FROM `user` WHERE nickname = #{nickname}")
	int countByNickname(@Param("nickname") String nickname);

	// 회원가입 저장
	@Insert("""
			INSERT INTO `user`
			(loginId, loginPw, nickname, email, userName, birth)
			VALUES
			(#{loginId}, #{loginPw}, #{nickname}, #{email}, #{userName}, #{birth})
			""")
	void insertUser(User user);

	// 아이디 찾기 기능
	@Select("""
			SELECT loginId
			    FROM `user`
			    WHERE userName = #{userName}
				AND email = #{email}
			    LIMIT 1
			""")
	String findLoginId(@Param("userName") String userName, @Param("email") String email);

	// 아이디 + 이메일 매칭 여부 확인 (비밀번호 찾기 용)
	@Select("""
			SELECT COUNT(*)
			    FROM `user`
			    WHERE loginId = #{loginId}
			      AND email = #{email}
			""")
	int matchIdAndEmail(@Param("loginId") String loginId, @Param("email") String email);

	// ⭐ 임시 비밀번호를 암호화하여 저장
	// ------------------------------
	@Update("""
			UPDATE `user`
			    SET loginPw = #{encodedPassword}
			    WHERE loginId = #{loginId}
			""")
	void updatePassword(@Param("loginId") String loginId, @Param("encodedPassword") String encodedPassword);

	// 비밀번호 수정을 위한 현재 비밀번호 가져오기
	@Select("""
			SELECT loginPw
				FROM `user`
				WHERE loginId = #{loginId}
			""")
	String getPassword(String loginId);

	// 로그인 되어있는 유저의 정보
	@Select("""
			SELECT id
					, loginId
					, nickname
					, email
					, userName
					, regDate
					, birth
					, profileImage
				FROM `user`
				WHERE loginId = #{loginUser}
			""")
	User getLoginUser(String loginUser);

	// 아이디 수정
	@Update("""
			UPDATE `user`
			    SET loginId = #{newLoginId}
			    WHERE loginId = #{currentLoginId}
			""")
	void updateLoginId(@Param("currentLoginId") String currentLoginId, @Param("newLoginId") String newLoginId);

	// 닉네임 수정
	@Update("""
			UPDATE `user`
				SET nickname = #{newNickname}
				WHERE loginId = #{currentLoginId}
			""")
	void updateNickname(@Param("currentLoginId") String currentLoginId, @Param("newNickname") String newNickname);

	// 이메일 수정
	@Update("""
			UPDATE `user`
				SET email = #{newEmail}
			    WHERE loginId = #{loginId}
			""")
	void updateEmail(@Param("loginId") String loginId, @Param("newEmail") String newEmail);


	// profileImage 업데이트
	@Update("""
			UPDATE `user`
				SET profileImage = #{profileImage}
				WHERE id = #{userId}
			""")
	void updateProfileImage(@Param("userId") int userId, @Param("profileImage") String profileImage);
	
	// 기본 이미지로 변경
	@Update("UPDATE `user` SET profileImage = #{path} WHERE id = #{userId}")
	void resetProfileImage(@Param("userId") int userId, @Param("path") String path);
	
	
	
}