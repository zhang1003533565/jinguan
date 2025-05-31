package com.example.suidao.admin.service;

import com.example.suidao.admin.entity.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface EnvironmentService extends BaseService<Environment, Long> {
    // 获取指定隧道的最新环境数据
    Environment getLatestByTunnelId(Long tunnelId);
    
    // 获取指定隧道的历史环境数据
    List<Environment> getHistoryByTunnelId(Long tunnelId, LocalDateTime startTime, LocalDateTime endTime);
    
    // 分页获取指定隧道的环境数据
    Page<Environment> getPageByTunnelId(Long tunnelId, Pageable pageable);

    // 获取最新环境数据
    Environment getLatestData();

    // 获取历史环境数据
    List<Environment> getHistoryData(String location, LocalDateTime startTime, LocalDateTime endTime);

    // 获取环境数据统计信息
    Map<String, Object> getStatistics(String location, String type, LocalDateTime date);

    // 获取报警阈值
    Map<String, Double> getAlarmThreshold();

    // 更新报警阈值
    void updateAlarmThreshold(Map<String, Double> thresholds);

    // 检查报警阈值
    Map<String, Object> checkAlarmThreshold(Map<String, Double> data);

    // 导出环境数据
    byte[] exportData(String location, LocalDateTime startTime, LocalDateTime endTime);
} 