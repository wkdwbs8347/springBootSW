package com.example.demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.EmailRequest;
import com.example.demo.dto.User;
import com.example.demo.service.UserService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    // 생성자 주입
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    // 아이디 중복체크
    @GetMapping("/checkId")
    public Map<String, Boolean> checkId(@RequestParam String loginId) {
        boolean available = this.userService.isIdAvailable(loginId);
        Map<String, Boolean> result = new HashMap<>();
        result.put("available", available);
        return result;
    }
    
    // 닉네임 중복체크
    @GetMapping("/checkNickname")
    public Map<String, Boolean> checkNickname(@RequestParam String nickname) {
        boolean available = this.userService.isNicknameAvailable(nickname);
        Map<String, Boolean> result = new HashMap<>();
        result.put("available", available);
        return result;
    }
    
    // 이메일 인증번호 전송
    @PostMapping("/emailSend")
    public ResponseEntity<?> sendEmail(@RequestBody EmailRequest request) {
        this.userService.sendAuthCode(request.getEmail());
        return ResponseEntity.ok("인증번호 전송 완료");
    }
    
    // 인증번호 확인
    @PostMapping("/emailVerify")
    public ResponseEntity<?> verifyEmail(@RequestBody EmailRequest request) {
        boolean verified = this.userService.verifyCode(request.getEmail(), request.getCode());
        if (verified) {
            return ResponseEntity.ok("인증 성공");
        } else {
            return ResponseEntity.badRequest().body("인증 실패");
        }
    }
    // 회원가입
    @PostMapping("/join")
    public Map<String, Object> join(@RequestBody User user) {
    	this.userService.registerUser(user);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return result;
    }
    
    // 로그인 검증
    @PostMapping("/login")
    public Map<String, Boolean> login(@RequestBody User user, HttpSession session) {
    	boolean loginChk = this.userService.loginChk(user);
    	Map<String, Boolean> result = new HashMap<>();
    	result.put("loginChk", loginChk);
    	
    	// 세션 생성 (검증을 통해 loginChk에 true 가 남는다면 로그인 검증에 성공했다는 의미)
    	if (loginChk) {
    		session.setAttribute("userLoginId", user.getLoginId());
    	}
    	return result;
    }
    
    // 로그인 여부 체크
    @GetMapping
    public Map<String, Object> loginCheck(HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        String loginUser = (String)session.getAttribute("userLoginId");

        if (loginUser != null) {
            map.put("isLogin", true);
            map.put("loginId", loginUser);
        } else {
            map.put("isLogin", false);
        }

        return map;
    }
    
    // 로그아웃
    @PostMapping("/logout")
    public Map<String, Object> logout(HttpSession session) {
    	// 사용자의 현재 세션을 종료 (세션 객체에 저장된 모든 정보 삭제, 해당 세션을 만료시키는 역할)
        session.invalidate();

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return result;
    }
    
}