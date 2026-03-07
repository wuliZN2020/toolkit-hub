package com.toolkit.hub.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.toolkit.hub.entity.Template;
import com.toolkit.hub.mapper.TemplateMapper;
import com.toolkit.hub.service.TemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Template service implementation
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TemplateServiceImpl implements TemplateService {

    private final TemplateMapper templateMapper;

    @Override
    public List<Template> getAllTemplates() {
        return templateMapper.selectList(
                new LambdaQueryWrapper<Template>()
                        .orderByAsc(Template::getSortOrder)
        );
    }

    @Override
    public List<Template> getTemplatesByPlatform(String platform) {
        return templateMapper.selectList(
                new LambdaQueryWrapper<Template>()
                        .eq(Template::getPlatform, platform)
                        .orderByAsc(Template::getSortOrder)
        );
    }

    @Override
    public Template getTemplateById(Long id) {
        return templateMapper.selectById(id);
    }
}
