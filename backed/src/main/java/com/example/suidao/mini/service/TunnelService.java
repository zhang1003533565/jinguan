package com.example.suidao.mini.service;

import com.example.suidao.mini.entity.Tunnel;
import java.util.List;

public interface TunnelService {
    List<Tunnel> list();
    Tunnel getById(Long id);
} 