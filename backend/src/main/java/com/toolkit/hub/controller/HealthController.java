package com.toolkit.hub.controller;

import com.toolkit.hub.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Health check controller
 *
 * @author zhangna
 */
@Tag(name = "Health Check", description = "System health check API")
@RestController
@RequestMapping("/health")
public class HealthController {

    @Operation(summary = "Health check")
    @GetMapping
    public Result<Map<String, Object>> health() {
        Map<String, Object> data = new HashMap<>();
        data.put("status", "UP");
        data.put("timestamp", LocalDateTime.now());
        data.put("application", "Toolkit Hub");
        data.put("version", "1.0.0");
        return Result.success(data);
    }

    @Operation(summary = "Test API")
    @GetMapping("/test")
    public Result<String> test() {
        return Result.success("Backend API is working!");
    }
}
