package com.example.demo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.example.demo.dto.MoveInRequest;
import com.example.demo.dto.Residence;

@Mapper
public interface ResidenceDao {

    // 입주 신청 insert
    @Insert("""
        INSERT INTO residence(buildingId, unitId, userId, floor, status, requestDate)
        VALUES(#{buildingId}, #{unitId}, #{userId}, #{floor}, 'waiting', NOW())
    """)
    void insertMoveIn(MoveInRequest req);

    // 신청 상태 업데이트 (승인)
    @Update("UPDATE residence SET status=#{status} WHERE id=#{id}")
    void updateStatus(@Param("id") int id, @Param("status") String status);

    // 신청 삭제 (거절)
    @Delete("DELETE FROM residence WHERE id=#{id}")
    void deleteMoveIn(int id);

    // 신청 상세 정보 조회
    @Select("""
    	    SELECT r.id, r.userId, r.buildingId, u.nickname, r.floor, 
    	           u.id as unitId, un.unitNumber, r.requestDate, r.status
    	    FROM residence r
    	    JOIN user u ON r.userId=u.id
    	    JOIN unit un ON r.unitId=un.id
    	    WHERE r.id=#{id}
    	""")
    	Residence detail(@Param("id") int id);

    // 건물별 신청 목록 (waiting 상태만)
    @Select("""
        SELECT r.id, r.userId, r.buildingId, u.nickname, r.floor, u.id as unitId, 
               un.unitNumber, r.requestDate, r.status
        FROM residence r
        JOIN user u ON r.userId=u.id
        JOIN unit un ON r.unitId=un.id
        WHERE r.buildingId=#{buildingId} AND r.status='waiting'
        ORDER BY r.requestDate DESC
    """)
    List<Residence> listApply(@Param("buildingId") int buildingId);

    // 건물 owner 조회 (알림 발송용)
    @Select("SELECT createdUserId FROM building WHERE id=#{buildingId}")
    Integer findBuildingOwner(int buildingId);
    
    @Insert("""
    	    INSERT IGNORE INTO building_member(buildingId, userId, role, joinedAt, active)
    	    VALUES(#{buildingId}, #{userId}, 'resident', NOW(), TRUE)
    	""")
    	void insertBuildingMember(@Param("buildingId") int buildingId, @Param("userId") int userId);
}


