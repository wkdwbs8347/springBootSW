package com.example.demo.service;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dao.UserDao;
import com.example.demo.dto.User;

@Service
public class UserService {

    private final UserDao dao;
    private final BCryptPasswordEncoder encoder;
    @Autowired
    private final JavaMailSender mailSender;
    
    
    // 생성자 주입
    public UserService(UserDao dao,JavaMailSender mailSender) {
        this.dao = dao;
        this.encoder = new BCryptPasswordEncoder();
        this.mailSender = mailSender;
    }

    // 이메일 인증 코드 저장
    private final ConcurrentHashMap<String, String> authCodeMap = new ConcurrentHashMap<>();
    
    // 인증코드 생성
    private String generateCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 6자리 코드
        return String.valueOf(code);
    }
    
    // 이메일 전송
    public void sendAuthCode(String toEmail) {
        String code = generateCode();
        authCodeMap.put(toEmail, code);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("[SweetHome] 이메일 인증 코드");
        message.setText("인증 코드는 " + code + " 입니다.");

        mailSender.send(message);
    }
    
    // 인증 코드 검증
    public boolean verifyCode(String email, String code) {
        String savedCode = authCodeMap.get(email);
        if (savedCode != null && savedCode.equals(code)) {
            authCodeMap.remove(email); // 인증 완료 후 삭제
            return true;
        }
        return false;
    }
    
    // 아이디 사용 가능 여부
    public boolean isIdAvailable(String loginId) {
        return dao.countByLoginId(loginId) == 0;
    }

    // 닉네임 사용 가능 여부
    public boolean isNicknameAvailable(String nickname) {
        return dao.countByNickname(nickname) == 0;
    }
    
    
    // 회원가입 처리
    public void registerUser(User user) {
        user.setLoginPw(encoder.encode(user.getLoginPw()));
        dao.insertUser(user);
    }

}