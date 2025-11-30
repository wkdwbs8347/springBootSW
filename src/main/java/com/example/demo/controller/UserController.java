package com.example.demo.controller;

import com.example.demo.dto.User;
import com.example.demo.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService service;

    // 생성자 주입
    public UserController(UserService service) {
        this.service = service;
    }
    
    // 아이디 중복체크
    @GetMapping("/checkId")
    public Map<String, Boolean> checkId(@RequestParam String loginId) {
        boolean available = service.isIdAvailable(loginId);
        Map<String, Boolean> result = new HashMap<>();
        result.put("available", available);
        return result;
    }
    
    // 닉네임 중복체크
    @GetMapping("/checkNickname")
    public Map<String, Boolean> checkNickname(@RequestParam String nickname) {
        boolean available = service.isNicknameAvailable(nickname);
        Map<String, Boolean> result = new HashMap<>();
        result.put("available", available);
        return result;
    }
    
    // 이메일 중복체크
    @GetMapping("/checkEmail")
    public Map<String, Boolean> checkEmail(@RequestParam String email) {
        boolean available = service.isEmailAvailable(email);
        Map<String, Boolean> result = new HashMap<>();
        result.put("available", available);
        return result;
    }    
    
    @PostMapping("/join")
    public Map<String, Object> join(@RequestBody User user) {
        service.registerUser(user);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return result;
    }
}