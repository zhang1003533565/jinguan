package com.example.suidao.admin.repository;

import com.example.suidao.admin.entity.Warning;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WarningRepository extends JpaRepository<Warning, Long> {
    List<Warning> findByTunnelIdOrderByCreatedAtDesc(Long tunnelId);
    Page<Warning> findByTunnelIdOrderByCreatedAtDesc(Long tunnelId, Pageable pageable);
    List<Warning> findByStatus(String status);
    List<Warning> findByTunnelIdAndStatus(Long tunnelId, String status);
    Page<Warning> findByTypeAndLevel(String type, String level, Pageable pageable);
    List<Warning> findByStatusOrderByCreatedAtDesc(String status);
    List<Warning> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime startTime, LocalDateTime endTime);
    List<Warning> findByTunnelIdAndStatusOrderByCreatedAtDesc(Long tunnelId, String status);
    List<Warning> findByTunnelIdAndCreatedAtBetweenOrderByCreatedAtDesc(Long tunnelId, LocalDateTime startTime, LocalDateTime endTime);
} 