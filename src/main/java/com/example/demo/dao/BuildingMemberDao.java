package com.example.demo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.example.demo.dto.BuildingMember;

@Mapper
public interface BuildingMemberDao {

	// 멤버 리스트
	@Select("""
			SELECT
			    bm.userId AS userId,
				u.nickname AS nickname,
				bm.role AS role,
				COALESCE(u_unit.unitNumber, 0) AS unitNumber,
				COALESCE(u_unit.floor, 0) AS `floor`,
				bm.unitId AS unitId
				    FROM building_member bm
				    JOIN `user` u ON bm.userId = u.id
				    LEFT JOIN unit u_unit ON bm.unitId = u_unit.id
				    WHERE bm.buildingId = #{buildingId} AND bm.active = TRUE
				    ORDER BY bm.role DESC, COALESCE(u_unit.floor, 0), COALESCE(u_unit.unitNumber, 0)
			""")
	List<BuildingMember> selectMembersByBuildingId(@Param("buildingId") int buildingId);

	// 멤버 상세
	@Select("""
			SELECT
				bm.userId AS userId,
				u.nickname AS nickname,
				bm.role AS role,
				COALESCE(u_unit.unitNumber, 0) AS unitNumber,
				COALESCE(u_unit.floor, 0) AS floor,
				u.profileImage AS profileImage,
				bm.joinedAt AS joinedAt
			    	FROM building_member bm
			    	JOIN `user` u ON bm.userId = u.id
			    	LEFT JOIN unit u_unit ON bm.unitId = u_unit.id
			    	WHERE bm.userId = #{userId}
			      	AND bm.unitId = #{unitId}
			      	AND bm.buildingId = #{id}
			      	AND bm.active = TRUE
			""")
	BuildingMember selectMemberByUserIdAndUnitId(@Param("id") int id, @Param("userId") int userId, @Param("unitId") int unitId);
}
