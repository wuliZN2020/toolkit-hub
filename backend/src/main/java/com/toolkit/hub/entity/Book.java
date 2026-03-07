package com.toolkit.hub.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Book entity
 */
@Data
@TableName("book")
public class Book {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * Douban book ID
     */
    private String doubanId;

    /**
     * Book title
     */
    private String title;

    /**
     * Author
     */
    private String author;

    /**
     * Cover image URL
     */
    private String coverUrl;

    /**
     * ISBN
     */
    private String isbn;

    /**
     * Publisher
     */
    private String publisher;

    /**
     * Publish date
     */
    private String publishDate;

    /**
     * Book summary
     */
    private String summary;

    /**
     * Douban rating
     */
    private String rating;

    /**
     * Logical delete flag (0: not deleted, 1: deleted)
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
