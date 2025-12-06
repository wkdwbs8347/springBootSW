package com.example.demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.EmailRequest;
import com.example.demo.dto.ResetPwReq;
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
	public Map<String, Object> login(@RequestBody User user, HttpSession session) {
		boolean loginChk = this.userService.loginChk(user);
		Map<String, Object> result = new HashMap<>();
		result.put("loginChk", loginChk);

		// 세션 생성 (검증을 통해 loginChk에 true 가 남는다면 로그인 검증에 성공했다는 의미)
		if (loginChk) {
			session.setAttribute("userLoginId", user.getLoginId());
			// 로그인 성공 직후 로그인한 유저 정보를 프론트에 넘겨주기 위함
			String loginUser = (String) session.getAttribute("userLoginId");
			User users = this.userService.getLoginUser(loginUser);
			result.put("loginUser", users);
		}
		return result;
	}

	// 로그인 여부 체크
	@GetMapping("/loginCheck")
	public Map<String, Object> loginCheck(HttpSession session) {
		Map<String, Object> map = new HashMap<>();
		String loginUser = (String) session.getAttribute("userLoginId");
		User users = this.userService.getLoginUser(loginUser);

		if (loginUser != null) {
			map.put("isLogin", true);
			map.put("loginId", loginUser);
			map.put("loginUser", users);
		} else {
			map.put("isLogin", false);
		}

		return map;
	}

	// 로그아웃
	@PostMapping("/doLogout")
	public Map<String, Object> logout(HttpSession session) {
		// 사용자의 현재 세션을 종료 (세션 객체에 저장된 모든 정보 삭제, 해당 세션을 만료시키는 역할)
		session.invalidate();

		Map<String, Object> result = new HashMap<>();
		result.put("success", true);
		return result;
	}

	// 아이디 찾기
	@GetMapping("/find-id")
	public ResponseEntity<?> findId(@RequestParam String userName, @RequestParam String email) {
		String id = userService.findLoginId(userName, email);
		if (id != null) {
			return ResponseEntity.ok(id);
		} else {
			return ResponseEntity.badRequest().body("정보와 일치하는 아이디가 없습니다.");
		}
	}

	// 임시 비밀번호 발급 & DB 업데이트
	@PostMapping("/reset-password")
	public ResponseEntity<?> resetPassword(@RequestBody ResetPwReq req) {
		boolean exists = userService.matchIdAndEmail(req.getLoginId(), req.getEmail());
		if (!exists) {
			return ResponseEntity.badRequest().body("아이디 또는 이메일이 일치하지 않습니다.");
		}
		String tempPw = userService.resetPassword(req.getLoginId()); // returns plain temp pw (not encoded)
		Map<String, String> map = new HashMap<>();
		map.put("tempPw", tempPw);
		return ResponseEntity.ok(map);
	}
	
	// 아이디 수정
	@PutMapping("/updateId")
	public ResponseEntity<?> updateId(@RequestBody Map<String, String> request, HttpSession session) {
	    String newLoginId = request.get("loginId");

	    if (newLoginId == null || newLoginId.trim().length() < 4) {
	        return ResponseEntity.badRequest().body("아이디는 4글자 이상이어야 합니다.");
	    }

	    // 현재 로그인 유저 세션 확인
	    String currentLoginId = (String) session.getAttribute("userLoginId");
	    if (currentLoginId == null) {
	        return ResponseEntity.status(401).body("로그인 상태가 아닙니다.");
	    }

	    // 서비스에서 업데이트 처리
	    try {
	        userService.updateLoginId(currentLoginId, newLoginId);
	        // 세션도 업데이트
	        session.setAttribute("userLoginId", newLoginId);
	        return ResponseEntity.ok("아이디가 수정되었습니다.");
	    } catch (RuntimeException e) {
	        return ResponseEntity.badRequest().body(e.getMessage());
	    }
	}
	
}
