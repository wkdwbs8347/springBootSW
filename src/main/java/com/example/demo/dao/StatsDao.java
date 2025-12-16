package com.example.demo.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface StatsDao {

    @Select("SELECT COUNT(*) FROM building")
    int getBuildingCount();

    @Select("SELECT COUNT(*) FROM user")
    int getUserCount();
}
