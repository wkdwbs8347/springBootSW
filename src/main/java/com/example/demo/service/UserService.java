package com.example.demo.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dao.UserDao;
import com.example.demo.dto.User;

@Service
public class UserService {

    private final UserDao dao;
    private final BCryptPasswordEncoder encoder;

    // 생성자 주입
    public UserService(UserDao dao) {
        this.dao = dao;
        this.encoder = new BCryptPasswordEncoder();
    }

    // 아이디 사용 가능 여부
    public boolean isIdAvailable(String loginId) {
        return dao.countByLoginId(loginId) == 0;
    }

    // 닉네임 사용 가능 여부
    public boolean isNicknameAvailable(String nickname) {
        return dao.countByNickname(nickname) == 0;
    }
    
    // 이메일 사용 가능 여부
    public boolean isEmailAvailable(String email) {
    	 return dao.countByEmail(email) == 0;
    }
    
    // 회원가입 처리
    public void registerUser(User user) {
        user.setLoginPw(encoder.encode(user.getLoginPw()));
        dao.insertUser(user);
    }

}