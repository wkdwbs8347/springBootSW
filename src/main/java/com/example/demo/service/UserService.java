package com.example.demo.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
		String encodePw = this.userDao.getPassword(user.getLoginId());
		
		// 만약 아이디가 존재하지 않을때는 쿼리 결과가 null 일것이기 때문에 그 상황에 대한 처리 
		if (encodePw == null) {
			return false;
		}
		
		// 가져온 암호화 된 비밀번호랑 사용자가 입력한 비밀번호와 비교하는 메서드
		return encoder.matches(user.getLoginPw(), encodePw);
	}
	
	// 아이디 찾기
	public String findLoginId(String userName, String email) {
		 return userDao.findLoginId(userName, email);
	}
	
	// 아이디 + 이메일 일치 여부
	public boolean matchIdAndEmail(String loginId, String email) {
		return userDao.matchIdAndEmail(loginId, email) > 0;
	}
	
	// 임시 비밀번호 생성 & 업데이트
	public String resetPassword(String loginId) {
		// 임시 비밀번호 생성
        String tempPw = generateTempPassword();

        // 암호화 후 DB 업데이트
        String encoded = encoder.encode(tempPw);
        userDao.updatePassword(loginId, encoded);

        return tempPw; // 프론트에게 보여줄 원본 비밀번호 반환
	}
	
	// 임시 비밀번호 생성 규칙
    private String generateTempPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random rnd = new Random();
        StringBuilder pw = new StringBuilder();

        for (int i = 0; i < 10; i++) {
            pw.append(chars.charAt(rnd.nextInt(chars.length())));
        }

        return pw.toString();
    }
    
    // 로그인 한 유저 정보 불러오기
	public User getLoginUser(String loginUser) {
		return this.userDao.getLoginUser(loginUser);
	}
	
	// 아이디 수정
	public void updateLoginId(String currentLoginId, String newLoginId) {
	    // 아이디 중복 체크
	    if (!isIdAvailable(newLoginId)) {
	        throw new RuntimeException("이미 사용 중인 아이디입니다.");
	    }

	    // DB 업데이트
	    userDao.updateLoginId(currentLoginId, newLoginId);
	}
	
	// 닉네임 수정
	public void updateNickname(String currentLoginId, String newNickname) {
		// 닉네임 중복 체크
		if (!isNicknameAvailable(newNickname)) {
			throw new RuntimeException("이미 사용 중인 닉네임입니다.");
		}
		userDao.updateNickname(currentLoginId, newNickname);
	}
	
	// 이메일 수정
	public void updateEmail(String loginId, String newEmail) {
	    userDao.updateEmail(loginId, newEmail);
	}
	
	// 비밀번호 수정
	public boolean changePassword(String loginId, String currentPw, String newPw) {
	    // DB에서 암호화된 기존 비밀번호 가져오기
	    String encodedPw = userDao.getPassword(loginId);

	    // 기존비밀번호 검증
	    if (!encoder.matches(currentPw, encodedPw)) {
	        return false;
	    }
	    
	    // null, 공백 검증
	    if(newPw == null || newPw.trim().isEmpty()){
	        throw new RuntimeException("새 비밀번호를 입력해주세요");
	    }

	    // 암호화 후 저장
	    String encodedNewPw = encoder.encode(newPw);
	    userDao.updatePassword(loginId, encodedNewPw);

	    return true;
	}
	
    
    // 프로필 이미지 업로드
    /**
     * 1) MultipartFile을 서버 로컬의 uploads 폴더에 저장
     * 2) DB에 /uploads/{filename} 형태로 저장
     * 3) 저장된 경로 반환
     */
    public String uploadProfileImage(int userId, MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("파일이 비어 있습니다.");
        }

        Path uploadDir = Paths.get("uploads");
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // 안전한 파일명 생성 (타임스탬프 + UUID + 원본이름)
        String original = file.getOriginalFilename();
        String ext = "";
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf("."));
        }
        String filename = System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0,8) + ext;
        Path filepath = uploadDir.resolve(filename);

        // 실제 파일 저장
        Files.copy(file.getInputStream(), filepath, StandardCopyOption.REPLACE_EXISTING);

        String dbPath = "http://localhost:8080/uploads/" + filename; // 프론트에서 접근 가능한 경로
        userDao.updateProfileImage(userId, dbPath);

        return dbPath;
    }
    
    // 기본 이미지로 변경
    public void resetProfileImage(int userId, String path) {
        userDao.resetProfileImage(userId, path); 
    }

}