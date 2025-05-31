package com.example.suidao.admin.controller;

import com.example.suidao.common.response.Result;
import com.example.suidao.admin.entity.Camera;
import com.example.suidao.admin.service.CameraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;

@RestController
@RequestMapping("/camera")
public class AdminCameraController {

    @Autowired
    private CameraService cameraService;

    @PostMapping
    public Result<Camera> create(@RequestBody Camera camera) {
        return Result.success(cameraService.save(camera));
    }

    @PutMapping("/{id}")
    public Result<Camera> update(@PathVariable Long id, @RequestBody Camera camera) {
        return Result.success(cameraService.update(id, camera));
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        cameraService.delete(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<Camera> getById(@PathVariable Long id) {
        return Result.success(cameraService.findById(id));
    }

    @GetMapping("/list")
    public Result<Page<Camera>> list(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(cameraService.findAll(PageRequest.of(page, size)));
    }

    @GetMapping("/status")
    public Result<Page<Camera>> listByStatus(
            @RequestParam String status,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(cameraService.findByStatus(status, PageRequest.of(page, size)));
    }

    @GetMapping("/online")
    public Result<List<Camera>> getOnlineCameras() {
        return Result.success(cameraService.findOnlineCameras());
    }

    @GetMapping("/offline")
    public Result<List<Camera>> getOfflineCameras() {
        return Result.success(cameraService.findOfflineCameras());
    }

    @PutMapping("/{id}/status")
    public Result<?> updateStatus(@PathVariable Long id, @RequestParam String status) {
        cameraService.updateStatus(id, status);
        return Result.success();
    }

    @PutMapping("/{id}/online-status")
    public Result<?> updateOnlineStatus(@PathVariable Long id, @RequestParam Boolean isOnline) {
        cameraService.updateOnlineStatus(id, isOnline);
        return Result.success();
    }

    @GetMapping("/{id}/stream-url")
    public Result<String> getStreamUrl(@PathVariable Long id) {
        return Result.success(cameraService.getStreamUrl(id));
    }

    @PostMapping("/{id}/ptz")
    public Result<?> controlPTZ(
            @PathVariable Long id,
            @RequestParam String command,
            @RequestParam(defaultValue = "1") Integer speed) {
        cameraService.controlPTZ(id, command, speed);
        return Result.success();
    }

    @GetMapping("/{id}/snapshot")
    public ResponseEntity<byte[]> takeSnapshot(@PathVariable Long id) {
        byte[] imageData = cameraService.takeSnapshot(id);
        return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_JPEG)
            .body(imageData);
    }

    @GetMapping(value = "/{id}/stream", produces = MediaType.MULTIPART_MIXED_VALUE)
    public ResponseEntity<StreamingResponseBody> streamVideo(@PathVariable Long id) {
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType("multipart/x-mixed-replace; boundary=--BoundaryString"))
            .body(outputStream -> {
                try {
                    while (true) {
                        byte[] imageData = cameraService.takeSnapshot(id);
                        outputStream.write(("--BoundaryString\r\n" +
                                "Content-Type: image/jpeg\r\n" +
                                "Content-Length: " + imageData.length + "\r\n\r\n").getBytes());
                        outputStream.write(imageData);
                        outputStream.write("\r\n".getBytes());
                        outputStream.flush();
                        Thread.sleep(100); // 每秒10帧
                    }
                } catch (Exception e) {
                    // 客户端断开连接时会抛出异常，这是正常的
                }
            });
    }
} 