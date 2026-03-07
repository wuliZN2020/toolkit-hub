package com.toolkit.hub.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Template entity
 */
@Data
@TableName("template")
public class Template {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * Template name
     */
    private String name;

    /**
     * Platform (wechat/xiaohongshu)
     */
    private String platform;

    /**
     * Template configuration (JSON format)
     */
    private String config;

    /**
     * Preview image URL
     */
    private String previewUrl;

    /**
     * Sort order
     */
    private Integer sortOrder;

    /**
     * Logical delete flag
     */
    @TableLogic
    private Integer deleted;

    /**
     * Create time
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * Update time
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
