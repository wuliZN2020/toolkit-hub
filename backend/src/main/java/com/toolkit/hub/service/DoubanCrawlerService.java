package com.toolkit.hub.service;

import com.toolkit.hub.dto.BookDTO;
import com.toolkit.hub.dto.QuoteDTO;
import java.util.List;

/**
 * Douban crawler service interface
 */
public interface DoubanCrawlerService {

    /**
     * Search books by keyword
     *
     * @param keyword search keyword
     * @return book list
     */
    List<BookDTO> searchBooks(String keyword);

    /**
     * Get book detail by Douban ID
     *
     * @param doubanId Douban book ID
     * @return book detail
     */
    BookDTO getBookDetail(String doubanId);

    /**
     * Get book quotes by Douban ID
     *
     * @param doubanId Douban book ID
     * @return quote list
     */
    List<QuoteDTO> getBookQuotes(String doubanId);

    /**
     * Get book quotes by Douban ID with pagination
     *
     * @param doubanId Douban book ID
     * @param start start index (0-based)
     * @param limit number of quotes to fetch
     * @return quote list
     */
    List<QuoteDTO> getBookQuotes(String doubanId, int start, int limit);
}
