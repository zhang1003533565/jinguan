package com.example.suidao.admin.controller;

import com.example.suidao.common.response.Result;
import com.example.suidao.admin.entity.Warning;
import com.example.suidao.admin.service.WarningService;
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
@RequestMapping("/warning")
public class AdminWarningController {

    @Autowired
    private WarningService warningService;

    @PostMapping
    public Result<Warning> create(@RequestBody Warning warning) {
        return Result.success(warningService.save(warning));
    }

    @PutMapping("/{id}")
    public Result<Warning> update(@PathVariable Long id, @RequestBody Warning warning) {
        return Result.success(warningService.update(id, warning));
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        warningService.delete(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<Warning> getById(@PathVariable Long id) {
        return Result.success(warningService.findById(id));
    }

    @GetMapping("/list")
    public Result<Page<Warning>> list(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(warningService.findAll(PageRequest.of(page, size)));
    }

    @GetMapping("/type-level")
    public Result<Page<Warning>> listByTypeAndLevel(
            @RequestParam String type,
            @RequestParam String level,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(warningService.findByTypeAndLevel(type, level, PageRequest.of(page, size)));
    }

    @GetMapping("/unhandled")
    public Result<List<Warning>> getUnhandledWarnings() {
        return Result.success(warningService.findUnhandledWarnings());
    }

    @GetMapping("/handled")
    public Result<List<Warning>> getHandledWarnings() {
        return Result.success(warningService.findHandledWarnings());
    }

    @PutMapping("/{id}/handle")
    public Result<?> handleWarning(
            @PathVariable Long id,
            @RequestParam String handler,
            @RequestParam String handleMethod) {
        warningService.handleWarning(id, handler, handleMethod);
        return Result.success();
    }

    @GetMapping("/statistics")
    public Result<Map<String, Long>> getWarningStatistics(
            @RequestParam LocalDateTime startTime,
            @RequestParam LocalDateTime endTime) {
        return Result.success(warningService.getWarningStatistics(startTime, endTime));
    }

    @GetMapping("/trend")
    public Result<Map<String, List<Long>>> getWarningTrend(
            @RequestParam String type,
            @RequestParam String level,
            @RequestParam LocalDateTime startTime,
            @RequestParam LocalDateTime endTime) {
        return Result.success(warningService.getWarningTrend(type, level, startTime, endTime));
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportWarnings(
            @RequestParam LocalDateTime startTime,
            @RequestParam LocalDateTime endTime) {
        byte[] data = warningService.exportWarnings(startTime, endTime);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=warning_records.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }

    @PostMapping("/{id}/notify")
    public Result<?> sendWarningNotification(@PathVariable Long id) {
        Warning warning = warningService.findById(id);
        warningService.sendWarningNotification(warning);
        return Result.success();
    }
} 