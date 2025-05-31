package com.example.suidao.admin.controller;

import com.example.suidao.common.response.Result;
import com.example.suidao.admin.entity.Camera;
import com.example.suidao.admin.entity.Device;
import com.example.suidao.admin.entity.Environment;
import com.example.suidao.admin.entity.Tunnel;
import com.example.suidao.admin.entity.Warning;
import com.example.suidao.admin.service.TunnelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/tunnel")
public class AdminTunnelController {

    @Autowired
    private TunnelService tunnelService;

    @PostMapping
    public Result<Tunnel> create(@RequestBody Tunnel tunnel) {
        return Result.success(tunnelService.save(tunnel));
    }

    @PutMapping("/{id}")
    public Result<Tunnel> update(@PathVariable Long id, @RequestBody Tunnel tunnel) {
        return Result.success(tunnelService.update(id, tunnel));
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        tunnelService.delete(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<Tunnel> getById(@PathVariable Long id) {
        return Result.success(tunnelService.findById(id));
    }

    @GetMapping("/list")
    public Result<Page<Tunnel>> list(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(tunnelService.findAll(PageRequest.of(page, size)));
    }

    @GetMapping("/status")
    public Result<Page<Tunnel>> listByStatus(
            @RequestParam String status,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(tunnelService.findByStatus(status, PageRequest.of(page, size)));
    }

    @GetMapping("/{id}/devices")
    public Result<List<Device>> getDevices(@PathVariable Long id) {
        return Result.success(tunnelService.getDevices(id));
    }

    @GetMapping("/{id}/cameras")
    public Result<List<Camera>> getCameras(@PathVariable Long id) {
        return Result.success(tunnelService.getCameras(id));
    }

    @GetMapping("/{id}/environment/latest")
    public Result<Environment> getLatestEnvironment(@PathVariable Long id) {
        return Result.success(tunnelService.getLatestEnvironment(id));
    }

    @GetMapping("/{id}/environment/history")
    public Result<List<Environment>> getEnvironmentHistory(
            @PathVariable Long id,
            @RequestParam LocalDateTime startTime,
            @RequestParam LocalDateTime endTime) {
        return Result.success(tunnelService.getEnvironmentHistory(id, startTime, endTime));
    }

    @GetMapping("/{id}/warnings")
    public Result<List<Warning>> getWarnings(
            @PathVariable Long id,
            @RequestParam LocalDateTime startTime,
            @RequestParam LocalDateTime endTime) {
        return Result.success(tunnelService.getWarnings(id, startTime, endTime));
    }

    @PostMapping("/{tunnelId}/device/{deviceId}")
    public Result<?> addDevice(@PathVariable Long tunnelId, @PathVariable Long deviceId) {
        tunnelService.addDevice(tunnelId, deviceId);
        return Result.success();
    }

    @DeleteMapping("/{tunnelId}/device/{deviceId}")
    public Result<?> removeDevice(@PathVariable Long tunnelId, @PathVariable Long deviceId) {
        tunnelService.removeDevice(tunnelId, deviceId);
        return Result.success();
    }

    @PostMapping("/{tunnelId}/camera/{cameraId}")
    public Result<?> addCamera(@PathVariable Long tunnelId, @PathVariable Long cameraId) {
        tunnelService.addCamera(tunnelId, cameraId);
        return Result.success();
    }

    @DeleteMapping("/{tunnelId}/camera/{cameraId}")
    public Result<?> removeCamera(@PathVariable Long tunnelId, @PathVariable Long cameraId) {
        tunnelService.removeCamera(tunnelId, cameraId);
        return Result.success();
    }
} 