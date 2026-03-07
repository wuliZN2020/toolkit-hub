package com.toolkit.hub.controller;

import com.toolkit.hub.common.result.Result;
import com.toolkit.hub.entity.Template;
import com.toolkit.hub.service.TemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Template Controller
 */
@Slf4j
@RestController
@RequestMapping("/template")
@RequiredArgsConstructor
@Tag(name = "Template API", description = "Template management")
public class TemplateController {

    private final TemplateService templateService;

    @GetMapping("/list")
    @Operation(summary = "Get all templates", description = "Get all available templates")
    public Result<List<Template>> getAllTemplates() {
        log.info("Get all templates");
        List<Template> templates = templateService.getAllTemplates();
        return Result.success(templates);
    }

    @GetMapping("/list/{platform}")
    @Operation(summary = "Get templates by platform", description = "Get templates filtered by platform")
    public Result<List<Template>> getTemplatesByPlatform(
            @Parameter(description = "Platform (wechat/xiaohongshu)", required = true)
            @PathVariable String platform) {
        log.info("Get templates by platform: {}", platform);
        List<Template> templates = templateService.getTemplatesByPlatform(platform);
        return Result.success(templates);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get template detail", description = "Get template detail by ID")
    public Result<Template> getTemplateById(
            @Parameter(description = "Template ID", required = true)
            @PathVariable Long id) {
        log.info("Get template by id: {}", id);
        Template template = templateService.getTemplateById(id);
        return Result.success(template);
    }
}
