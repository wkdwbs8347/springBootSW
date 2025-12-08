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
	@Insert("INSERT INTO building (created_usr, name, address, total_floor) "
			+ "VALUES (#{createdUsr}, #{name}, #{address}, #{totalFloor})")
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
}