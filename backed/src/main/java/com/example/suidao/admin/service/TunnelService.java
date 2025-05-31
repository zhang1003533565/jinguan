package com.example.suidao.admin.service;

import com.example.suidao.admin.entity.Camera;
import com.example.suidao.admin.entity.Device;
import com.example.suidao.admin.entity.Environment;
import com.example.suidao.admin.entity.Tunnel;
import com.example.suidao.admin.entity.Warning;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;

public interface TunnelService extends BaseService<Tunnel, Long> {
    // 根据状态查询隧道列表
    Page<Tunnel> findByStatus(String status, Pageable pageable);
    
    // 获取隧道关联的设备列表
    List<Device> getDevices(Long tunnelId);
    
    // 获取隧道关联的摄像头列表
    List<Camera> getCameras(Long tunnelId);
    
    // 获取隧道环境数据
    Environment getLatestEnvironment(Long tunnelId);
    
    // 获取隧道历史环境数据
    List<Environment> getEnvironmentHistory(Long tunnelId, LocalDateTime startTime, LocalDateTime endTime);
    
    // 获取隧道预警记录
    List<Warning> getWarnings(Long tunnelId, LocalDateTime startTime, LocalDateTime endTime);
    
    // 添加设备到隧道
    void addDevice(Long tunnelId, Long deviceId);
    
    // 添加摄像头到隧道
    void addCamera(Long tunnelId, Long cameraId);
    
    // 从隧道移除设备
    void removeDevice(Long tunnelId, Long deviceId);
    
    // 从隧道移除摄像头
    void removeCamera(Long tunnelId, Long cameraId);
} 