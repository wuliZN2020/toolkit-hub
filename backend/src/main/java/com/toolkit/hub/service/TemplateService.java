package com.toolkit.hub.service;

import com.toolkit.hub.entity.Template;
import java.util.List;

/**
 * Template service interface
 */
public interface TemplateService {

    /**
     * Get all templates
     *
     * @return template list
     */
    List<Template> getAllTemplates();

    /**
     * Get templates by platform
     *
     * @param platform platform (wechat/xiaohongshu)
     * @return template list
     */
    List<Template> getTemplatesByPlatform(String platform);

    /**
     * Get template by ID
     *
     * @param id template ID
     * @return template
     */
    Template getTemplateById(Long id);
}
