package com.example.suidao.admin.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BaseService<T, ID> {
    T save(T entity);
    T update(ID id, T entity);
    void delete(ID id);
    T findById(ID id);
    Page<T> findAll(Pageable pageable);
} 