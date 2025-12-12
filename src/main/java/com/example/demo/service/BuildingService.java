package com.example.demo.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dao.BuildingDao;
import com.example.demo.dto.Building;
import com.example.demo.dto.Unit;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BuildingService {

    private final BuildingDao buildingDao;
    private final String UPLOAD_DIR = "uploads"; // 실제 서버 폴더 경로

    /**
     * 1️⃣ 건물 등록 후 2️⃣ 층별 단위 호수 자동 생성
     */
    @Transactional
    public void registerBuilding(Building dto) {

        // 중복 체크
        int exists = buildingDao.countByNameAndAddress(dto.getName(), dto.getAddress());
        if (exists > 0) {
            throw new RuntimeException("이미 등록된 건물입니다.");
        }
        
        // 건물 등록
        buildingDao.insertBuilding(dto);
        int buildingId = dto.getId();

        // 층별 호수 생성
        List<Unit> units = new ArrayList<>();
        for (int floor = 1; floor <= dto.getTotalFloor(); floor++) {
            for (int i = 1; i <= dto.getUnitNumber(); i++) {
                Unit unit = new Unit();
                unit.setBuildingId(buildingId);
                unit.setFloor(floor);
                unit.setUnitNumber(floor * 100 + i);
                units.add(unit);
            }
        }
        buildingDao.insertUnits(units);
    }
    
    /**
     * 주소로 건물 목록 조회
     */
    public List<Building> getBuildingsByAddress(String address) {
        return buildingDao.selectByAddress(address);
    }

    /**
     * 건물 ID로 층/호수 목록 조회
     */
    public List<Unit> getUnitsByBuilding(int buildingId) {
        return buildingDao.selectUnitsByBuilding(buildingId);
    }
    
    // owner 리스트 페이지
    public List<Building> getBuildingsByOwner(Integer userId) {
        return buildingDao.selectByOwnerList(userId);
    }
    
    // resident 리스트 페이지
    public List<Building> getBuildingsByResident(Integer userId) {
        return buildingDao.selectByResidentList(userId);
    }
    
    // userId가 해당 building의 등록자인지 확인
    public boolean isOwnerOfBuilding(Integer userId, Integer buildingId) {
        return buildingDao.isOwnerOfBuilding(buildingId, userId) > 0;
    }
    
    
 // owner는 buildingId 기준, resident는 unitId 기준
    public Building getBuildingDetail(Integer userId, Integer buildingId, Integer unitId, boolean isOwner) {
    	System.out.println(buildingId);
    	System.out.println(userId);
    	System.out.println(unitId);
    	System.out.println(isOwner);
        if (isOwner) {
            if (buildingId == null) return null; // buildingId 필수
            return buildingDao.selectByOwner(userId, buildingId);
        } else {
            if (unitId == null) return null; // unitId 필수
            return buildingDao.selectByResident(userId, unitId);
        }
    }
    
    /**
     * 건물 이미지 업로드
     */
    public String uploadBuildingImage(MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) throw new RuntimeException("파일이 비어 있습니다.");

        Path uploadDir = Paths.get("uploads");
        if (!Files.exists(uploadDir)) Files.createDirectories(uploadDir);

        String original = file.getOriginalFilename();
        String ext = "";
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf("."));
        }
        String filename = System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0,8) + ext;
        Path filepath = uploadDir.resolve(filename);

        Files.copy(file.getInputStream(), filepath, StandardCopyOption.REPLACE_EXISTING);

        return "http://localhost:8080/uploads/" + filename;
    }
    
    /**
     * 기존 이미지 삭제 후 새 이미지 업로드
     */
    public String updateBuildingImage(int buildingId, MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) throw new RuntimeException("파일이 비어 있습니다.");

        // 기존 이미지 URL 가져오기
        Building building = buildingDao.selectBuildingById(buildingId);
        String oldImageUrl = building.getProfileImage();

        // 기존 이미지 파일 삭제 (기본 이미지는 삭제하지 않음)
        if (oldImageUrl != null && !oldImageUrl.contains("defaultBuildingImg")) {
            Path oldFilePath = Paths.get(UPLOAD_DIR, oldImageUrl.substring(oldImageUrl.lastIndexOf("/") + 1));
            if (Files.exists(oldFilePath)) {
                Files.delete(oldFilePath);
            }
        }

        // 새 이미지 저장
        if (!Files.exists(Paths.get(UPLOAD_DIR))) {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
        }

        String original = file.getOriginalFilename();
        String ext = "";
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf("."));
        }

        String filename = System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8) + ext;
        Path filepath = Paths.get(UPLOAD_DIR, filename);
        Files.copy(file.getInputStream(), filepath, StandardCopyOption.REPLACE_EXISTING);

        String newImageUrl = "http://localhost:8080/uploads/" + filename;

        // DB 업데이트
        building.setProfileImage(newImageUrl);
        buildingDao.updateBuildingImage(building);

        return newImageUrl;
    }
    
}