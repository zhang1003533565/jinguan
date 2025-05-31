package com.example.suidao.admin.repository;

import com.example.suidao.admin.entity.Device;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    List<Device> findByTunnelIdOrderByCreatedAtDesc(Long tunnelId);
    
    Page<Device> findByTunnelIdOrderByCreatedAtDesc(Long tunnelId, Pageable pageable);
    
    List<Device> findByStatus(String status);
    
    Page<Device> findByTypeAndStatus(String type, String status, Pageable pageable);
    
    List<Device> findByIsOnlineTrue();
    
    List<Device> findByIsOnlineFalse();
} 