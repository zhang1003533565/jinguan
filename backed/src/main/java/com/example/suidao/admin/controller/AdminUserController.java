package com.example.suidao.admin.controller;

import com.example.suidao.common.response.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminUserController {
    
    @GetMapping("/user/list")
    public Result<?> getUserList() {
        // 管理后台获取用户列表的逻辑
        return Result.success("这是管理后台用户列表接口");
    }
} 