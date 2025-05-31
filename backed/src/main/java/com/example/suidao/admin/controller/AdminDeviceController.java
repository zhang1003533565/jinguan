package com.example.suidao.admin.controller;

import com.example.suidao.common.response.Result;
import com.example.suidao.admin.entity.Device;
import com.example.suidao.admin.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/device")
public class AdminDeviceController {

    @Autowired
    private DeviceService deviceService;

    @PostMapping
    public Result<Device> create(@RequestBody Device device) {
        return Result.success(deviceService.save(device));
    }

    @PutMapping("/{id}")
    public Result<Device> update(@PathVariable Long id, @RequestBody Device device) {
        return Result.success(deviceService.update(id, device));
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        deviceService.delete(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<Device> getById(@PathVariable Long id) {
        return Result.success(deviceService.findById(id));
    }

    @GetMapping("/list")
    public Result<Page<Device>> list(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(deviceService.findAll(PageRequest.of(page, size)));
    }

    @GetMapping("/type-status")
    public Result<Page<Device>> listByTypeAndStatus(
            @RequestParam String type,
            @RequestParam String status,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(deviceService.findByTypeAndStatus(type, status, PageRequest.of(page, size)));
    }

    @GetMapping("/online")
    public Result<List<Device>> getOnlineDevices() {
        return Result.success(deviceService.findOnlineDevices());
    }

    @GetMapping("/offline")
    public Result<List<Device>> getOfflineDevices() {
        return Result.success(deviceService.findOfflineDevices());
    }

    @PutMapping("/{id}/status")
    public Result<?> updateStatus(@PathVariable Long id, @RequestParam String status) {
        deviceService.updateStatus(id, status);
        return Result.success();
    }

    @PutMapping("/{id}/maintenance")
    public Result<?> recordMaintenance(@PathVariable Long id) {
        deviceService.recordMaintenance(id);
        return Result.success();
    }

    @PutMapping("/{id}/online-status")
    public Result<?> updateOnlineStatus(@PathVariable Long id, @RequestParam Boolean isOnline) {
        deviceService.updateOnlineStatus(id, isOnline);
        return Result.success();
    }
} 