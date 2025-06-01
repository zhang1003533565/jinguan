package com.example.suidao.mini.service.impl;

import com.example.suidao.mini.entity.Tunnel;
import com.example.suidao.mini.service.TunnelService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

@Service("miniTunnelService")
@RequiredArgsConstructor
public class TunnelServiceImpl implements TunnelService {
    
    private final com.example.suidao.admin.service.TunnelService adminTunnelService;

    @Override
    public List<Tunnel> list() {
        return adminTunnelService.findAll(Pageable.unpaged()).getContent().stream()
            .map(this::convertToMiniTunnel)
            .collect(Collectors.toList());
    }

    @Override
    public Tunnel getById(Long id) {
        return convertToMiniTunnel(adminTunnelService.findById(id));
    }

    private Tunnel convertToMiniTunnel(com.example.suidao.admin.entity.Tunnel adminTunnel) {
        if (adminTunnel == null) {
            return null;
        }
        Tunnel miniTunnel = new Tunnel();
        BeanUtils.copyProperties(adminTunnel, miniTunnel);
        return miniTunnel;
    }
} 