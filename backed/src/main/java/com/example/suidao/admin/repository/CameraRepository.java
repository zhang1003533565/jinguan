package com.example.suidao.admin.repository;

import com.example.suidao.admin.entity.Camera;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CameraRepository extends JpaRepository<Camera, Long> {
    List<Camera> findByTunnelIdOrderByCreatedAtDesc(Long tunnelId);
    Page<Camera> findByTunnelIdOrderByCreatedAtDesc(Long tunnelId, Pageable pageable);
    List<Camera> findByStatus(String status);
    Page<Camera> findByStatus(String status, Pageable pageable);
    List<Camera> findByIsOnlineTrue();
    List<Camera> findByIsOnlineFalse();
    List<Camera> findByStatusOrderByCreatedAtDesc(String status);
} 