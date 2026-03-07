package com.toolkit.hub.service;

import com.toolkit.hub.dto.BookDTO;
import com.toolkit.hub.dto.QuoteDTO;
import com.toolkit.hub.entity.Book;
import java.util.List;

/**
 * Book service interface
 */
public interface BookService {

    /**
     * Search books from Douban and cache to database
     *
     * @param keyword search keyword
     * @return book list
     */
    List<BookDTO> searchBooks(String keyword);

    /**
     * Get book detail with quotes
     *
     * @param doubanId Douban book ID
     * @return book detail with quotes
     */
    BookDTO getBookWithQuotes(String doubanId);

    /**
     * Get book detail with quotes
     *
     * @param doubanId Douban book ID
     * @param limit quote limit
     * @return book detail with quotes
     */
    BookDTO getBookWithQuotes(String doubanId, int limit);

    /**
     * Save book to database
     *
     * @param bookDTO book data
     * @return saved book
     */
    Book saveBook(BookDTO bookDTO);

    /**
     * Get book quotes
     *
     * @param doubanId Douban book ID
     * @return quote list
     */
    List<QuoteDTO> getBookQuotes(String doubanId);

    /**
     * Get book quotes with pagination
     *
     * @param doubanId Douban book ID
     * @param start start index
     * @param limit page size
     * @return quote list
     */
    List<QuoteDTO> getBookQuotes(String doubanId, int start, int limit);
}
