package com.example.suidao.admin.service.impl;

import com.example.suidao.admin.entity.*;
import com.example.suidao.admin.repository.*;
import com.example.suidao.admin.service.TunnelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class TunnelServiceImpl implements TunnelService {
    @Autowired
    private TunnelRepository tunnelRepository;
    
    @Autowired
    private DeviceRepository deviceRepository;
    
    @Autowired
    private CameraRepository cameraRepository;
    
    @Autowired
    private EnvironmentRepository environmentRepository;
    
    @Autowired
    private WarningRepository warningRepository;

    @Override
    public Tunnel save(Tunnel tunnel) {
        return tunnelRepository.save(tunnel);
    }

    @Override
    public Tunnel update(Long id, Tunnel tunnel) {
        tunnel.setId(id);
        return tunnelRepository.save(tunnel);
    }

    @Override
    public void delete(Long id) {
        tunnelRepository.deleteById(id);
    }

    @Override
    public Tunnel findById(Long id) {
        return tunnelRepository.findById(id).orElse(null);
    }

    @Override
    public Page<Tunnel> findAll(Pageable pageable) {
        return tunnelRepository.findAll(pageable);
    }

    @Override
    public Page<Tunnel> findByStatus(String status, Pageable pageable) {
        return tunnelRepository.findByStatus(status, pageable);
    }

    @Override
    public List<Device> getDevices(Long tunnelId) {
        return deviceRepository.findByTunnelIdOrderByCreatedAtDesc(tunnelId);
    }

    @Override
    public List<Camera> getCameras(Long tunnelId) {
        return cameraRepository.findByTunnelIdOrderByCreatedAtDesc(tunnelId);
    }

    @Override
    public Environment getLatestEnvironment(Long tunnelId) {
        List<Environment> environments = environmentRepository.findByTunnelIdOrderByCreatedAtDesc(tunnelId);
        return environments.isEmpty() ? null : environments.get(0);
    }

    @Override
    public List<Environment> getEnvironmentHistory(Long tunnelId, LocalDateTime startTime, LocalDateTime endTime) {
        return environmentRepository.findByTunnelIdAndCreatedAtBetweenOrderByCreatedAtDesc(tunnelId, startTime, endTime);
    }

    @Override
    public List<Warning> getWarnings(Long tunnelId, LocalDateTime startTime, LocalDateTime endTime) {
        return warningRepository.findByTunnelIdAndCreatedAtBetweenOrderByCreatedAtDesc(tunnelId, startTime, endTime);
    }

    @Override
    public void addDevice(Long tunnelId, Long deviceId) {
        Tunnel tunnel = findById(tunnelId);
        Device device = deviceRepository.findById(deviceId).orElse(null);
        if (tunnel != null && device != null) {
            tunnel.addDevice(device);
            tunnelRepository.save(tunnel);
        }
    }

    @Override
    public void addCamera(Long tunnelId, Long cameraId) {
        Tunnel tunnel = findById(tunnelId);
        Camera camera = cameraRepository.findById(cameraId).orElse(null);
        if (tunnel != null && camera != null) {
            tunnel.addCamera(camera);
            tunnelRepository.save(tunnel);
        }
    }

    @Override
    public void removeDevice(Long tunnelId, Long deviceId) {
        Tunnel tunnel = findById(tunnelId);
        Device device = deviceRepository.findById(deviceId).orElse(null);
        if (tunnel != null && device != null) {
            tunnel.removeDevice(device);
            tunnelRepository.save(tunnel);
        }
    }

    @Override
    public void removeCamera(Long tunnelId, Long cameraId) {
        Tunnel tunnel = findById(tunnelId);
        Camera camera = cameraRepository.findById(cameraId).orElse(null);
        if (tunnel != null && camera != null) {
            tunnel.removeCamera(camera);
            tunnelRepository.save(tunnel);
        }
    }
} 