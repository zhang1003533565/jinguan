package com.example.suidao.mini.controller;

import com.example.suidao.common.response.Result;
import com.example.suidao.admin.entity.Warning;
import com.example.suidao.admin.service.WarningService;
import com.example.suidao.mini.entity.Tunnel;
import com.example.suidao.mini.service.TunnelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/warning")
@RequiredArgsConstructor
@Tag(name = "小程序-告警管理")
public class WarningController {

    private final WarningService warningService;
    
    @Qualifier("miniTunnelService")
    private final TunnelService tunnelService;
    
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @PostMapping("")
    @Operation(summary = "提交告警")
    public Result<Warning> submitWarning(@RequestBody WarningSubmitDTO request) {
        Warning warning = new Warning();
        warning.setType(request.getType());
        warning.setLevel(request.getLevel());
        warning.setDescription(request.getDescription());
        warning.setTunnelId(request.getTunnelId());
        warning.setStatus(request.getStatus());
        warning.setHandleMethod(request.getHandleMethod());
        warning.setHandler(request.getHandler());
        
        // 处理时间字段
        if (request.getHandleTime() != null && !request.getHandleTime().trim().isEmpty()) {
            warning.setHandleTime(LocalDateTime.parse(request.getHandleTime(), formatter));
        }
        
        warning.setCreatedAt(LocalDateTime.now());
        warning.setUpdatedAt(LocalDateTime.now());
        
        Warning saved = warningService.save(warning);
        return Result.success(saved);
    }

    @GetMapping("/tunnels")
    @Operation(summary = "获取所有隧道")
    public Result<List<Tunnel>> getAllTunnels() {
        List<Tunnel> tunnels = tunnelService.list();
        return Result.success(tunnels);
    }
} 