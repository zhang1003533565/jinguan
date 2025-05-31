package com.example.suidao.admin.controller;

import com.example.suidao.admin.entity.Tunnel;
import com.example.suidao.admin.repository.TunnelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/tunnel")
public class TunnelController {

    @Autowired
    private TunnelRepository tunnelRepository;

    @GetMapping("/list")
    public ResponseEntity<List<Tunnel>> getAllTunnels() {
        return ResponseEntity.ok(tunnelRepository.findAllByOrderByCreatedAtDesc());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tunnel> getTunnelById(@PathVariable Long id) {
        return tunnelRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Tunnel>> getTunnelsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(tunnelRepository.findByStatus(status));
    }

    @PostMapping
    public ResponseEntity<Tunnel> createTunnel(@RequestBody Tunnel tunnel) {
        return ResponseEntity.ok(tunnelRepository.save(tunnel));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tunnel> updateTunnel(@PathVariable Long id, @RequestBody Tunnel tunnel) {
        if (!tunnelRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        tunnel.setId(id);
        return ResponseEntity.ok(tunnelRepository.save(tunnel));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Tunnel> updateTunnelStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> status) {
        return tunnelRepository.findById(id)
                .map(tunnel -> {
                    tunnel.setStatus(status.get("status"));
                    return ResponseEntity.ok(tunnelRepository.save(tunnel));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTunnel(@PathVariable Long id) {
        if (!tunnelRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        tunnelRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Tunnel>> searchTunnels(@RequestParam String keyword) {
        return ResponseEntity.ok(
            tunnelRepository.findByNameContainingOrDescriptionContaining(keyword, keyword)
        );
    }
} 