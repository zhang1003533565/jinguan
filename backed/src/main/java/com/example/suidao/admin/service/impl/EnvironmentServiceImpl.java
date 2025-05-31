package com.example.suidao.admin.service.impl;

import com.example.suidao.admin.entity.Environment;
import com.example.suidao.admin.repository.EnvironmentRepository;
import com.example.suidao.admin.service.EnvironmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class EnvironmentServiceImpl implements EnvironmentService {
    @Autowired
    private EnvironmentRepository environmentRepository;

    @Override
    public Environment save(Environment environment) {
        return environmentRepository.save(environment);
    }

    @Override
    public Environment update(Long id, Environment environment) {
        environment.setId(id);
        return environmentRepository.save(environment);
    }

    @Override
    public void delete(Long id) {
        environmentRepository.deleteById(id);
    }

    @Override
    public Environment findById(Long id) {
        return environmentRepository.findById(id).orElse(null);
    }

    @Override
    public Page<Environment> findAll(Pageable pageable) {
        return environmentRepository.findAll(pageable);
    }

    @Override
    public Environment getLatestByTunnelId(Long tunnelId) {
        List<Environment> environments = environmentRepository.findByTunnelIdOrderByCreatedAtDesc(tunnelId);
        return environments.isEmpty() ? null : environments.get(0);
    }

    @Override
    public List<Environment> getHistoryByTunnelId(Long tunnelId, LocalDateTime startTime, LocalDateTime endTime) {
        return environmentRepository.findByTunnelIdAndCreatedAtBetweenOrderByCreatedAtDesc(tunnelId, startTime, endTime);
    }

    @Override
    public Page<Environment> getPageByTunnelId(Long tunnelId, Pageable pageable) {
        return environmentRepository.findByTunnelIdOrderByCreatedAtDesc(tunnelId, pageable);
    }

    @Override
    public Environment getLatestData() {
        return environmentRepository.findFirstByOrderByCreatedAtDesc();
    }

    @Override
    public List<Environment> getHistoryData(String location, LocalDateTime startTime, LocalDateTime endTime) {
        return environmentRepository.findByLocationAndCreatedAtBetweenOrderByCreatedAtDesc(location, startTime, endTime);
    }

    @Override
    public Map<String, Object> getStatistics(String location, String type, LocalDateTime date) {
        // 实现环境数据统计
        Map<String, Object> statistics = new HashMap<>();
        List<Environment> data = getHistoryData(location, date.withHour(0), date.withHour(23).withMinute(59).withSecond(59));
        
        // 计算平均值、最大值、最小值等
        if (!data.isEmpty()) {
            double sum = 0;
            double max = Double.MIN_VALUE;
            double min = Double.MAX_VALUE;
            
            for (Environment env : data) {
                double value = getValueByType(env, type);
                sum += value;
                max = Math.max(max, value);
                min = Math.min(min, value);
            }
            
            statistics.put("average", sum / data.size());
            statistics.put("max", max);
            statistics.put("min", min);
            statistics.put("count", data.size());
        }
        
        return statistics;
    }

    private double getValueByType(Environment env, String type) {
        switch (type) {
            case "temperature":
                return env.getTemperature();
            case "humidity":
                return env.getHumidity();
            case "co2":
                return env.getCo2();
            default:
                return 0;
        }
    }

    @Override
    public Map<String, Double> getAlarmThreshold() {
        // 从配置或数据库中获取报警阈值
        Map<String, Double> thresholds = new HashMap<>();
        thresholds.put("temperature_max", 35.0);
        thresholds.put("temperature_min", 10.0);
        thresholds.put("humidity_max", 80.0);
        thresholds.put("humidity_min", 20.0);
        thresholds.put("co2_max", 1000.0);
        return thresholds;
    }

    @Override
    public void updateAlarmThreshold(Map<String, Double> thresholds) {
        // 更新报警阈值到配置或数据库
        // TODO: 实现阈值更新逻辑
    }

    @Override
    public Map<String, Object> checkAlarmThreshold(Map<String, Double> data) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Double> thresholds = getAlarmThreshold();
        
        // 检查温度
        if (data.containsKey("temperature")) {
            double temp = data.get("temperature");
            if (temp > thresholds.get("temperature_max")) {
                result.put("temperature", "高温报警");
            } else if (temp < thresholds.get("temperature_min")) {
                result.put("temperature", "低温报警");
            }
        }
        
        // 检查湿度
        if (data.containsKey("humidity")) {
            double humidity = data.get("humidity");
            if (humidity > thresholds.get("humidity_max")) {
                result.put("humidity", "高湿报警");
            } else if (humidity < thresholds.get("humidity_min")) {
                result.put("humidity", "低湿报警");
            }
        }
        
        // 检查二氧化碳
        if (data.containsKey("co2")) {
            double co2 = data.get("co2");
            if (co2 > thresholds.get("co2_max")) {
                result.put("co2", "二氧化碳超标报警");
            }
        }
        
        return result;
    }

    @Override
    public byte[] exportData(String location, LocalDateTime startTime, LocalDateTime endTime) {
        List<Environment> data = getHistoryData(location, startTime, endTime);
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("环境数据");
            
            // 创建标题行
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("时间");
            headerRow.createCell(1).setCellValue("位置");
            headerRow.createCell(2).setCellValue("温度(°C)");
            headerRow.createCell(3).setCellValue("湿度(%)");
            headerRow.createCell(4).setCellValue("CO2(ppm)");
            
            // 填充数据
            int rowNum = 1;
            for (Environment env : data) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(env.getCreatedAt().toString());
                row.createCell(1).setCellValue(env.getLocation());
                row.createCell(2).setCellValue(env.getTemperature());
                row.createCell(3).setCellValue(env.getHumidity());
                row.createCell(4).setCellValue(env.getCo2());
            }
            
            // 导出为字节数组
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("导出数据失败", e);
        }
    }
} 