package com.example.demo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.example.demo.dto.BuildingRegister;
import com.example.demo.dto.Unit;

@Mapper
public interface BuildingDao {

	// 건물 등록
	@Insert("INSERT INTO building (createdUserId, name, address, totalFloor) "
			+ "VALUES (#{createdUserId}, #{name}, #{address}, #{totalFloor})")
	@Options(useGeneratedKeys = true, keyProperty = "id")
	void insertBuilding(BuildingRegister paylode);

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
    List<BuildingRegister> selectByAddress(@Param("address") String address);

    // 특정 건물의 층/호수 조회
    @Select("SELECT id, buildingId, floor, unitNumber, currentResidentId FROM unit WHERE buildingId = #{buildingId}")
    List<Unit> selectUnitsByBuilding(@Param("buildingId") int buildingId);
}