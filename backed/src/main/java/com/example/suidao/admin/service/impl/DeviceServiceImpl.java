package com.example.suidao.admin.service.impl;

import com.example.suidao.admin.entity.Device;
import com.example.suidao.admin.repository.DeviceRepository;
import com.example.suidao.admin.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class DeviceServiceImpl implements DeviceService {
    @Autowired
    private DeviceRepository deviceRepository;

    @Override
    public Device save(Device device) {
        return deviceRepository.save(device);
    }

    @Override
    public Device update(Long id, Device device) {
        device.setId(id);
        return deviceRepository.save(device);
    }

    @Override
    public void delete(Long id) {
        deviceRepository.deleteById(id);
    }

    @Override
    public Device findById(Long id) {
        return deviceRepository.findById(id).orElse(null);
    }

    @Override
    public Page<Device> findAll(Pageable pageable) {
        return deviceRepository.findAll(pageable);
    }

    @Override
    public List<Device> getByTunnelId(Long tunnelId) {
        return deviceRepository.findByTunnelIdOrderByCreatedAtDesc(tunnelId);
    }

    @Override
    public List<Device> getByStatus(String status) {
        return deviceRepository.findByStatus(status);
    }

    @Override
    public Page<Device> getPageByTunnelId(Long tunnelId, Pageable pageable) {
        return deviceRepository.findByTunnelIdOrderByCreatedAtDesc(tunnelId, pageable);
    }

    @Override
    public List<Device> findOnlineDevices() {
        return deviceRepository.findByIsOnlineTrue();
    }

    @Override
    public List<Device> findOfflineDevices() {
        return deviceRepository.findByIsOnlineFalse();
    }

    @Override
    public void updateStatus(Long id, String status) {
        Device device = findById(id);
        if (device != null) {
            device.setStatus(status);
            deviceRepository.save(device);
        }
    }

    @Override
    public void recordMaintenance(Long id) {
        Device device = findById(id);
        if (device != null) {
            device.setLastMaintenanceTime(LocalDateTime.now());
            deviceRepository.save(device);
        }
    }

    @Override
    public void updateOnlineStatus(Long id, Boolean isOnline) {
        Device device = findById(id);
        if (device != null) {
            device.setIsOnline(isOnline);
            device.setLastOnlineTime(isOnline ? LocalDateTime.now() : device.getLastOnlineTime());
            deviceRepository.save(device);
        }
    }

    @Override
    public Page<Device> findByTypeAndStatus(String type, String status, Pageable pageable) {
        return deviceRepository.findByTypeAndStatus(type, status, pageable);
    }
} 