package com.example.suidao.mini.controller;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "告警提交DTO")
public class WarningSubmitDTO {
    
    @Schema(description = "隧道ID")
    @NotNull(message = "隧道ID不能为空")
    private Long tunnelId;
    
    @Schema(description = "问题类型")
    @NotBlank(message = "问题类型不能为空")
    private String type;
    
    @Schema(description = "问题级别")
    @NotBlank(message = "问题级别不能为空")
    private String level;
    
    @Schema(description = "问题描述")
    @NotBlank(message = "问题描述不能为空")
    private String description;
    
    @Schema(description = "处理方式")
    private String handleMethod;
    
    @Schema(description = "处理时间")
    private String handleTime;
    
    @Schema(description = "处理人")
    private String handler;
    
    @Schema(description = "状态")
    @NotBlank(message = "状态不能为空")
    private String status;
} 