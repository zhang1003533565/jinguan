package com.example.suidao.admin.service;

import com.example.suidao.admin.entity.Camera;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface CameraService {
    Camera save(Camera camera);
    Camera update(Long id, Camera camera);
    void delete(Long id);
    Camera findById(Long id);
    Page<Camera> findAll(Pageable pageable);
    List<Camera> getByTunnelId(Long tunnelId);
    List<Camera> getByStatus(String status);
    Page<Camera> getPageByTunnelId(Long tunnelId, Pageable pageable);
    List<Camera> findOnlineCameras();
    List<Camera> findOfflineCameras();
    void updateStatus(Long id, String status);
    void updateOnlineStatus(Long id, Boolean isOnline);
    String getStreamUrl(Long id);
    void controlPTZ(Long id, String command, Integer speed);
    byte[] takeSnapshot(Long id);
    Page<Camera> findByStatus(String status, Pageable pageable);
} 