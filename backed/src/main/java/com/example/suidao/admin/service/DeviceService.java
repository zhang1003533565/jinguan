package com.example.suidao.admin.service;

import com.example.suidao.admin.entity.Device;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface DeviceService extends BaseService<Device, Long> {
    // 获取指定隧道的设备列表
    List<Device> getByTunnelId(Long tunnelId);
    
    // 获取指定状态的设备列表
    List<Device> getByStatus(String status);
    
    // 分页获取设备列表
    Page<Device> getPageByTunnelId(Long tunnelId, Pageable pageable);

    // 根据类型和状态查找设备
    Page<Device> findByTypeAndStatus(String type, String status, Pageable pageable);

    // 获取在线设备列表
    List<Device> findOnlineDevices();

    // 获取离线设备列表
    List<Device> findOfflineDevices();

    // 更新设备状态
    void updateStatus(Long id, String status);

    // 记录设备维护
    void recordMaintenance(Long id);

    // 更新设备在线状态
    void updateOnlineStatus(Long id, Boolean isOnline);
} 