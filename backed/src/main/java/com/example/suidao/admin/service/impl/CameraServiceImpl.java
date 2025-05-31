package com.example.suidao.admin.service.impl;

import com.example.suidao.admin.entity.Camera;
import com.example.suidao.admin.repository.CameraRepository;
import com.example.suidao.admin.service.CameraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class CameraServiceImpl implements CameraService {
    @Autowired
    private CameraRepository cameraRepository;

    @Override
    public Camera save(Camera camera) {
        return cameraRepository.save(camera);
    }

    @Override
    public Camera update(Long id, Camera camera) {
        camera.setId(id);
        return cameraRepository.save(camera);
    }

    @Override
    public void delete(Long id) {
        cameraRepository.deleteById(id);
    }

    @Override
    public Camera findById(Long id) {
        return cameraRepository.findById(id).orElse(null);
    }

    @Override
    public Page<Camera> findAll(Pageable pageable) {
        return cameraRepository.findAll(pageable);
    }

    @Override
    public List<Camera> getByTunnelId(Long tunnelId) {
        return cameraRepository.findByTunnelIdOrderByCreatedAtDesc(tunnelId);
    }

    @Override
    public List<Camera> getByStatus(String status) {
        return cameraRepository.findByStatusOrderByCreatedAtDesc(status);
    }

    @Override
    public Page<Camera> getPageByTunnelId(Long tunnelId, Pageable pageable) {
        return cameraRepository.findByTunnelIdOrderByCreatedAtDesc(tunnelId, pageable);
    }

    @Override
    public List<Camera> findOnlineCameras() {
        return cameraRepository.findByIsOnlineTrue();
    }

    @Override
    public List<Camera> findOfflineCameras() {
        return cameraRepository.findByIsOnlineFalse();
    }

    @Override
    public void updateStatus(Long id, String status) {
        Camera camera = findById(id);
        if (camera != null) {
            camera.setStatus(status);
            cameraRepository.save(camera);
        }
    }

    @Override
    public void updateOnlineStatus(Long id, Boolean isOnline) {
        Camera camera = findById(id);
        if (camera != null) {
            camera.setIsOnline(isOnline);
            camera.setLastOnlineTime(isOnline ? LocalDateTime.now() : camera.getLastOnlineTime());
            cameraRepository.save(camera);
        }
    }

    @Override
    public String getStreamUrl(Long id) {
        Camera camera = findById(id);
        return camera != null ? camera.getStreamUrl() : null;
    }

    @Override
    public void controlPTZ(Long id, String command, Integer speed) {
        Camera camera = findById(id);
        if (camera != null && camera.getIsOnline()) {
            // TODO: 实现PTZ控制逻辑
            // 这里需要调用摄像头的SDK或API来实现实际的PTZ控制
            switch (command.toLowerCase()) {
                case "up":
                    // 向上转动
                    break;
                case "down":
                    // 向下转动
                    break;
                case "left":
                    // 向左转动
                    break;
                case "right":
                    // 向右转动
                    break;
                case "zoomin":
                    // 放大
                    break;
                case "zoomout":
                    // 缩小
                    break;
            }
        }
    }

    @Override
    public byte[] takeSnapshot(Long id) {
        Camera camera = findById(id);
        if (camera != null && camera.getIsOnline()) {
            // TODO: 实现截图逻辑
            // 这里需要调用摄像头的SDK或API来实现实际的截图功能
            // 返回图片的字节数组
            return new byte[0]; // 临时返回空数组
        }
        return null;
    }

    @Override
    public Page<Camera> findByStatus(String status, Pageable pageable) {
        return cameraRepository.findByStatus(status, pageable);
    }
} 