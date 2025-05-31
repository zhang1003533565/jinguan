package com.example.suidao.admin.entity;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonBackReference;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "camera")
public class Camera {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tunnel_id")
    private Long tunnelId;

    @Column(name = "name")
    private String name;

    @Column(name = "location")
    private String location;

    @Column(name = "status")
    private String status;

    @Column(name = "is_online")
    private Boolean isOnline;

    @Column(name = "stream_url")
    private String streamUrl;

    @Column(name = "last_online_time")
    private LocalDateTime lastOnlineTime;

    @Column(name = "last_maintenance_time")
    private LocalDateTime lastMaintenanceTime;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @JsonBackReference
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