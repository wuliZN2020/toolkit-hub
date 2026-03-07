package com.toolkit.hub.dto;

import lombok.Data;

/**
 * Quote DTO
 */
@Data
public class QuoteDTO {
    private Long id;
    private Long bookId;
    private String content;
    private Integer pageNum;
    private Integer likes;
}
