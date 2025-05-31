package com.example.suidao.admin.controller;

import com.example.suidao.common.response.Result;
import com.example.suidao.admin.entity.Environment;
import com.example.suidao.admin.service.EnvironmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/environment")
public class AdminEnvironmentController {

    @Autowired
    private EnvironmentService environmentService;

    @PostMapping
    public Result<Environment> create(@RequestBody Environment environment) {
        return Result.success(environmentService.save(environment));
    }

    @PutMapping("/{id}")
    public Result<Environment> update(@PathVariable Long id, @RequestBody Environment environment) {
        return Result.success(environmentService.update(id, environment));
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        environmentService.delete(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<Environment> getById(@PathVariable Long id) {
        return Result.success(environmentService.findById(id));
    }

    @GetMapping("/list")
    public Result<Page<Environment>> list(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(environmentService.findAll(PageRequest.of(page, size)));
    }

    @GetMapping("/latest")
    public Result<Environment> getLatestData() {
        return Result.success(environmentService.getLatestData());
    }

    @GetMapping("/history")
    public Result<List<Environment>> getHistoryData(
            @RequestParam String location,
            @RequestParam LocalDateTime startTime,
            @RequestParam LocalDateTime endTime) {
        return Result.success(environmentService.getHistoryData(location, startTime, endTime));
    }

    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics(
            @RequestParam String location,
            @RequestParam String type,
            @RequestParam LocalDateTime date) {
        return Result.success(environmentService.getStatistics(location, type, date));
    }

    @GetMapping("/threshold")
    public Result<Map<String, Double>> getAlarmThreshold() {
        return Result.success(environmentService.getAlarmThreshold());
    }

    @PutMapping("/threshold")
    public Result<?> updateAlarmThreshold(@RequestBody Map<String, Double> thresholds) {
        environmentService.updateAlarmThreshold(thresholds);
        return Result.success();
    }

    @PostMapping("/check-alarm")
    public Result<Map<String, Object>> checkAlarmThreshold(@RequestBody Map<String, Double> data) {
        return Result.success(environmentService.checkAlarmThreshold(data));
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportData(
            @RequestParam String location,
            @RequestParam LocalDateTime startTime,
            @RequestParam LocalDateTime endTime) {
        byte[] data = environmentService.exportData(location, startTime, endTime);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=environment_data.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }
} 