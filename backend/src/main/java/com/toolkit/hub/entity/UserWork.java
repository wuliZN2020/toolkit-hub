package com.toolkit.hub.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * User work entity
 */
@Data
@TableName("user_work")
public class UserWork {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * Book ID
     */
    private Long bookId;

    /**
     * Template ID
     */
    private Long templateId;

    /**
     * Formatted content
     */
    private String content;

    /**
     * Target platform
     */
    private String platform;

    /**
     * Generated image URL
     */
    private String imageUrl;

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
