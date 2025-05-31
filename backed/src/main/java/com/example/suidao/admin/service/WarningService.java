package com.example.suidao.admin.service;

import com.example.suidao.admin.entity.Warning;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface WarningService extends BaseService<Warning, Long> {
    // 获取指定隧道的预警记录
    List<Warning> getByTunnelId(Long tunnelId);
    
    // 获取指定状态的预警记录
    List<Warning> getByStatus(String status);
    
    // 获取指定隧道和状态的预警记录
    List<Warning> getByTunnelIdAndStatus(Long tunnelId, String status);
    
    // 分页获取预警记录
    Page<Warning> getPageByTunnelId(Long tunnelId, Pageable pageable);
    
    // 处理预警（更新状态）
    Warning handleWarning(Long id, String status);

    // 根据类型和级别分页查找预警
    Page<Warning> findByTypeAndLevel(String type, String level, Pageable pageable);

    // 获取未处理的预警列表
    List<Warning> findUnhandledWarnings();

    // 获取已处理的预警列表
    List<Warning> findHandledWarnings();

    // 处理预警
    void handleWarning(Long id, String handler, String handleMethod);

    // 获取预警统计信息
    Map<String, Long> getWarningStatistics(LocalDateTime startTime, LocalDateTime endTime);

    // 获取预警趋势
    Map<String, List<Long>> getWarningTrend(String type, String level, LocalDateTime startTime, LocalDateTime endTime);

    // 导出预警记录
    byte[] exportWarnings(LocalDateTime startTime, LocalDateTime endTime);

    // 发送预警通知
    void sendWarningNotification(Warning warning);
} 