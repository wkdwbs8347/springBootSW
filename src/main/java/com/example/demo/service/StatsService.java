package com.example.demo.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.demo.dao.StatsDao;

@Service
public class StatsService {

    private final StatsDao statsDao;

    public StatsService(StatsDao statsDao) {
        this.statsDao = statsDao;
    }

    public Map<String, Integer> getStats() {
        Map<String, Integer> result = new HashMap<>();
        result.put("buildingCount", statsDao.getBuildingCount());
        result.put("userCount", statsDao.getUserCount());
        return result;
    }
}
