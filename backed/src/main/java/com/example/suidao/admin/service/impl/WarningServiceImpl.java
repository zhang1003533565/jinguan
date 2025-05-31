package com.example.suidao.admin.service.impl;

import com.example.suidao.admin.entity.Warning;
import com.example.suidao.admin.repository.WarningRepository;
import com.example.suidao.admin.service.WarningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class WarningServiceImpl implements WarningService {
    @Autowired
    private WarningRepository warningRepository;

    @Override
    public Warning save(Warning warning) {
        return warningRepository.save(warning);
    }

    @Override
    public Warning update(Long id, Warning warning) {
        warning.setId(id);
        return warningRepository.save(warning);
    }

    @Override
    public void delete(Long id) {
        warningRepository.deleteById(id);
    }

    @Override
    public Warning findById(Long id) {
        return warningRepository.findById(id).orElse(null);
    }

    @Override
    public Page<Warning> findAll(Pageable pageable) {
        return warningRepository.findAll(pageable);
    }

    @Override
    public List<Warning> getByTunnelId(Long tunnelId) {
        return warningRepository.findByTunnelIdOrderByCreatedAtDesc(tunnelId);
    }

    @Override
    public List<Warning> getByStatus(String status) {
        return warningRepository.findByStatusOrderByCreatedAtDesc(status);
    }

    @Override
    public List<Warning> getByTunnelIdAndStatus(Long tunnelId, String status) {
        return warningRepository.findByTunnelIdAndStatusOrderByCreatedAtDesc(tunnelId, status);
    }

    @Override
    public Page<Warning> getPageByTunnelId(Long tunnelId, Pageable pageable) {
        return warningRepository.findByTunnelIdOrderByCreatedAtDesc(tunnelId, pageable);
    }

    @Override
    public Warning handleWarning(Long id, String status) {
        Warning warning = findById(id);
        if (warning != null) {
            warning.setStatus(status);
            return warningRepository.save(warning);
        }
        return null;
    }

    @Override
    public List<Warning> findUnhandledWarnings() {
        return warningRepository.findByStatusOrderByCreatedAtDesc("未处理");
    }

    @Override
    public List<Warning> findHandledWarnings() {
        return warningRepository.findByStatusOrderByCreatedAtDesc("已处理");
    }

    @Override
    public void handleWarning(Long id, String handler, String handleMethod) {
        Warning warning = findById(id);
        if (warning != null) {
            warning.setStatus("已处理");
            warning.setHandler(handler);
            warning.setHandleMethod(handleMethod);
            warning.setHandleTime(LocalDateTime.now());
            warningRepository.save(warning);
        }
    }

    @Override
    public Map<String, Long> getWarningStatistics(LocalDateTime startTime, LocalDateTime endTime) {
        List<Warning> warnings = warningRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(startTime, endTime);
        
        // 按类型统计预警数量
        Map<String, Long> statistics = warnings.stream()
            .collect(Collectors.groupingBy(Warning::getType, Collectors.counting()));
        
        // 添加总数
        statistics.put("total", (long) warnings.size());
        
        // 按状态统计
        Map<String, Long> statusStats = warnings.stream()
            .collect(Collectors.groupingBy(Warning::getStatus, Collectors.counting()));
        statistics.putAll(statusStats);
        
        return statistics;
    }

    @Override
    public Map<String, List<Long>> getWarningTrend(String type, String level, LocalDateTime startTime, LocalDateTime endTime) {
        List<Warning> warnings = warningRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(startTime, endTime);
        
        // 按小时统计预警趋势
        Map<String, List<Long>> trend = new HashMap<>();
        List<Long> counts = new ArrayList<>();
        
        // 初始化24小时的数据
        for (int i = 0; i < 24; i++) {
            final int hour = i;
            long count = warnings.stream()
                .filter(w -> w.getCreatedAt().getHour() == hour)
                .filter(w -> type == null || w.getType().equals(type))
                .filter(w -> level == null || w.getLevel().equals(level))
                .count();
            counts.add(count);
        }
        
        trend.put("hourly", counts);
        return trend;
    }

    @Override
    public byte[] exportWarnings(LocalDateTime startTime, LocalDateTime endTime) {
        List<Warning> warnings = warningRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(startTime, endTime);
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("预警记录");
            
            // 创建标题行
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("时间");
            headerRow.createCell(1).setCellValue("类型");
            headerRow.createCell(2).setCellValue("级别");
            headerRow.createCell(3).setCellValue("描述");
            headerRow.createCell(4).setCellValue("状态");
            headerRow.createCell(5).setCellValue("处理人");
            headerRow.createCell(6).setCellValue("处理方法");
            headerRow.createCell(7).setCellValue("处理时间");
            
            // 填充数据
            int rowNum = 1;
            for (Warning warning : warnings) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(warning.getCreatedAt().toString());
                row.createCell(1).setCellValue(warning.getType());
                row.createCell(2).setCellValue(warning.getLevel());
                row.createCell(3).setCellValue(warning.getDescription());
                row.createCell(4).setCellValue(warning.getStatus());
                row.createCell(5).setCellValue(warning.getHandler());
                row.createCell(6).setCellValue(warning.getHandleMethod());
                row.createCell(7).setCellValue(warning.getHandleTime() != null ? warning.getHandleTime().toString() : "");
            }
            
            // 导出为字节数组
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("导出数据失败", e);
        }
    }

    @Override
    public void sendWarningNotification(Warning warning) {
        // TODO: 实现预警通知逻辑
        // 这里可以集成短信、邮件、钉钉等通知方式
        // 1. 根据预警级别选择通知方式
        // 2. 根据预警类型选择通知模板
        // 3. 发送通知
        
        // 示例：打印日志
        System.out.println("发送预警通知：" + warning.getType() + " - " + warning.getDescription());
    }

    @Override
    public Page<Warning> findByTypeAndLevel(String type, String level, Pageable pageable) {
        return warningRepository.findByTypeAndLevel(type, level, pageable);
    }
} 