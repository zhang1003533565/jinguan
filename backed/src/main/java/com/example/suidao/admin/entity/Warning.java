package com.example.suidao.admin.entity;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "warning")
public class Warning {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tunnel_id")
    private Long tunnelId;

    @Column(name = "type")
    private String type;

    @Column(name = "level")
    private String level;

    @Column(name = "description")
    private String description;

    @Column(name = "status")
    private String status;

    @Column(name = "handler")
    private String handler;

    @Column(name = "handle_method")
    private String handleMethod;

    @Column(name = "handle_time")
    private LocalDateTime handleTime;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tunnel_id", insertable = false, updatable = false)
    private Tunnel tunnel;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 