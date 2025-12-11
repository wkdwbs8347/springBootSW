package com.example.demo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.example.demo.dto.Building;
import com.example.demo.dto.Unit;

@Mapper
public interface BuildingDao {

	// 건물 등록
	@Insert("INSERT INTO building (createdUserId, name, address, totalFloor) "
			+ "VALUES (#{createdUserId}, #{name}, #{address}, #{totalFloor})")
	@Options(useGeneratedKeys = true, keyProperty = "id")
	void insertBuilding(Building paylode);

	// 단위 호수 batch insert
	@Insert({ "<script>", "INSERT INTO unit (buildingId, `floor`, unitNumber) VALUES ",
			"<foreach collection='list' item='unit' separator=','>",
			"(#{unit.buildingId}, #{unit.floor}, #{unit.unitNumber})", "</foreach>", "</script>" })
	void insertUnits(List<Unit> units);

	// 이름+주소로 존재 여부 체크
	@Select("SELECT COUNT(*) FROM building WHERE name = #{name} AND address = #{address}")
	int countByNameAndAddress(@Param("name") String name, @Param("address") String address);

	// 주소별 건물 조회 (MoveInPage)
	@Select("SELECT id, name, address, totalFloor FROM building WHERE address = #{address}")
	List<Building> selectByAddress(@Param("address") String address);

	// 특정 건물의 층/호수 조회
	@Select("SELECT id, buildingId, floor, unitNumber, currentResidentId FROM unit WHERE buildingId = #{buildingId}")
	List<Unit> selectUnitsByBuilding(@Param("buildingId") int buildingId);

	// owner 리스트 페이지
	@Select("SELECT id, name, address, totalFloor, createdUserId, regDate "
			+ "FROM building WHERE createdUserId = #{userId}")
	List<Building> selectByOwnerList(@Param("userId") Integer userId);

	// resident 리스트 페이지
	@Select("""
			SELECT b.id AS buildingId,
			       b.name,
			       b.address,
			       b.totalFloor,
			       b.createdUserId,
			       bm.joinedAt AS regDate,
			       u.id AS unitId,
			       u.floor,
			       u.unitNumber
			FROM building b
			JOIN building_member bm
			  ON bm.buildingId = b.id
			 AND bm.active = TRUE
			 AND bm.role = 'resident'
			JOIN unit u
			  ON u.buildingId = b.id
			 AND u.currentResidentId = bm.userId
			WHERE bm.userId = #{userId}
			GROUP BY b.id, u.id, u.unitNumber
						""")
	List<Building> selectByResidentList(@Param("userId") Integer userId);

	// owner 용 건물 상세 조회
	@Select("""
			SELECT b.id, b.`name`, b.address, b.totalFloor, b.createdUserId, b.regDate, u.nickname, COUNT(un.Id) AS unitCnt
				FROM building AS b
				JOIN `user` AS u
				ON b.createdUserId = u.id
				JOIN unit AS un
				ON un.buildingId = b.id
				WHERE b.createdUserId = #{userId} AND b.id = #{buildingId}
				""")
	Building selectByOwner(@Param("userId") int userId, @Param("buildingId") int buildingId);

	// resident 용 건물 상세 조회
	@Select("""
			 SELECT
			     b.id AS buildingId,
			     b.name,
			     b.address,
			     b.totalFloor,
			     b.createdUserId,
			     bm.joinedAt AS regDate,
			     un.id AS unitId,
			     un.floor,
			     un.unitNumber,
			     u.nickname
			 FROM building_member bm
			 JOIN building b
			   ON bm.buildingId = b.id
			 JOIN unit un
			   ON bm.unitId = un.id
			 JOIN `user` u
			   ON bm.userId = u.id
			 WHERE bm.userId = #{userId}
			   AND bm.unitId = #{unitId}
			   AND bm.active = TRUE
			   AND bm.role = 'resident'
			""")
	Building selectByResident(@Param("userId") Integer userId, @Param("unitId") Integer unitId);
}
