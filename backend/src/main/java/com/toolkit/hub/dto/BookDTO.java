package com.toolkit.hub.dto;

import lombok.Data;
import java.util.List;

/**
 * Book DTO with quotes
 */
@Data
public class BookDTO {
    private Long id;
    private String doubanId;
    private String title;
    private String author;
    private String coverUrl;
    private String isbn;
    private String publisher;
    private String publishDate;
    private String summary;
    private String rating;  // 豆瓣评分
    private List<QuoteDTO> quotes;
}
