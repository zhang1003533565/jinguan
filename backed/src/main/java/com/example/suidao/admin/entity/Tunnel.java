package com.example.suidao.admin.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tunnel")
public class Tunnel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;            // 隧道名称
    private Double length;          // 隧道长度
    private Double width;           // 隧道宽度
    private Double height;          // 隧道高度
    
    private String location;        // 地理位置
    private String status;          // 运营状态
    
    private String type;            // 隧道类型
    
    @Column(length = 1000)
    private String description;     // 描述信息
    
    private LocalDateTime constructionTime;  // 建设时间
    private LocalDateTime openTime;          // 开通时间
    
    private String manager;         // 负责人
    private String contact;         // 联系方式
    
    @JsonManagedReference
    @OneToMany(mappedBy = "tunnel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Device> devices = new ArrayList<>();  // 关联设备
    
    @JsonManagedReference
    @OneToMany(mappedBy = "tunnel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Camera> cameras = new ArrayList<>();  // 关联摄像头
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;    // 创建时间
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;    // 更新时间
    
    // 添加设备
    public void addDevice(Device device) {
        devices.add(device);
        device.setTunnel(this);
    }
    
    // 移除设备
    public void removeDevice(Device device) {
        devices.remove(device);
        device.setTunnel(null);
    }
    
    // 添加摄像头
    public void addCamera(Camera camera) {
        cameras.add(camera);
        camera.setTunnel(this);
    }
    
    // 移除摄像头
    public void removeCamera(Camera camera) {
        cameras.remove(camera);
        camera.setTunnel(null);
    }

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