package com.example.suidao.admin.entity;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "environment")
public class Environment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tunnel_id")
    private Long tunnelId;

    @Column(name = "location")
    private String location;

    @Column(name = "temperature")
    private Double temperature;

    @Column(name = "humidity")
    private Double humidity;

    @Column(name = "co2")
    private Double co2;

    @Column(name = "pm25")
    private Double pm25;

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