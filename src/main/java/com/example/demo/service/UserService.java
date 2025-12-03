package com.example.demo.service;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dao.UserDao;
import com.example.demo.dto.User;

@Service
public class UserService {

    private final UserDao userDao;
    private final BCryptPasswordEncoder encoder;
    @Autowired
    private final JavaMailSender mailSender;
    
    
    // 생성자 주입
    public UserService(UserDao userDao,JavaMailSender mailSender) {
        this.userDao = userDao;
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
        return this.userDao.countByLoginId(loginId) == 0;
    }

    // 닉네임 사용 가능 여부
    public boolean isNicknameAvailable(String nickname) {
        return this.userDao.countByNickname(nickname) == 0;
    }
    
    
    // 회원가입 처리
    public void registerUser(User user) {
    	// 비밀번호 암호화
        user.setLoginPw(encoder.encode(user.getLoginPw()));
        this.userDao.insertUser(user);
    }
    
    // 로그인 검증
	public boolean loginChk(User user) {
		// 암호화 된 비밀번호 가져오기
		String encodePw = this.userDao.loginChk(user);
		
		// 만약 아이디가 존재하지 않을때는 쿼리 결과가 null 일것이기 때문에 그 상황에 대한 처리 
		if (encodePw == null) {
			return false;
		}
		
		// 가져온 암호화 된 비밀번호랑 사용자가 입력한 비밀번호와 비교하는 메서드
		return encoder.matches(user.getLoginPw(), encodePw);
	}

}