package com.toolkit.hub.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Quote entity
 */
@Data
@TableName("quote")
public class Quote {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * Book ID
     */
    private Long bookId;

    /**
     * Quote content
     */
    private String content;

    /**
     * Page number
     */
    private Integer pageNum;

    /**
     * Number of likes
     */
    private Integer likes;

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
