package com.example.suidao.mini.controller;

import com.example.suidao.common.response.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MiniUserController {
    
    @GetMapping("/user/info")
    public Result<?> getUserInfo() {
        // 小程序获取用户信息的逻辑
        return Result.success("这是小程序用户信息接口");
    }
} 