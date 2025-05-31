package com.example.suidao.admin.repository;

import com.example.suidao.admin.entity.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EnvironmentRepository extends JpaRepository<Environment, Long> {
    List<Environment> findByTunnelIdOrderByCreatedAtDesc(Long tunnelId);
    
    Page<Environment> findByTunnelIdOrderByCreatedAtDesc(Long tunnelId, Pageable pageable);
    
    List<Environment> findByTunnelIdAndCreatedAtBetweenOrderByCreatedAtDesc(Long tunnelId, LocalDateTime startTime, LocalDateTime endTime);
    
    Environment findFirstByOrderByCreatedAtDesc();
    
    List<Environment> findByLocationAndCreatedAtBetweenOrderByCreatedAtDesc(String location, LocalDateTime startTime, LocalDateTime endTime);
} 