package com.toolkit.hub;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Toolkit Hub Application
 *
 * @author zhangna
 * @date 2026-03-03
 */
@SpringBootApplication
@MapperScan("com.toolkit.hub.mapper")
public class ToolkitHubApplication {

    public static void main(String[] args) {
        SpringApplication.run(ToolkitHubApplication.class, args);
        System.out.println("====================================");
        System.out.println("Toolkit Hub started successfully!");
        System.out.println("Swagger UI: http://localhost:8080/doc.html");
        System.out.println("====================================");
    }
}
