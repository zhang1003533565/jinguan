package com.example.suidao.admin.repository;

import com.example.suidao.admin.entity.Tunnel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TunnelRepository extends JpaRepository<Tunnel, Long> {
    
    List<Tunnel> findAllByOrderByCreatedAtDesc();
    
    List<Tunnel> findByStatus(String status);
    
    Page<Tunnel> findByStatus(String status, Pageable pageable);
    
    List<Tunnel> findByNameContainingOrDescriptionContaining(String name, String description);
} 