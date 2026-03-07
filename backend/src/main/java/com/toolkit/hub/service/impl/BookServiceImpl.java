package com.toolkit.hub.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.toolkit.hub.dto.BookDTO;
import com.toolkit.hub.dto.QuoteDTO;
import com.toolkit.hub.entity.Book;
import com.toolkit.hub.entity.Quote;
import com.toolkit.hub.mapper.BookMapper;
import com.toolkit.hub.mapper.QuoteMapper;
import com.toolkit.hub.service.BookService;
import com.toolkit.hub.service.DoubanCrawlerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Book service implementation
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookMapper bookMapper;
    private final QuoteMapper quoteMapper;
    private final DoubanCrawlerService doubanCrawlerService;

    @Override
    public List<BookDTO> searchBooks(String keyword) {
        log.info("Searching books with keyword: {}", keyword);
        return doubanCrawlerService.searchBooks(keyword);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BookDTO getBookWithQuotes(String doubanId) {
        return getBookWithQuotes(doubanId, 50);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BookDTO getBookWithQuotes(String doubanId, int limit) {
        log.info("Getting book with quotes: {}, limit: {}", doubanId, limit);

        // Check if book exists in database
        Book book = bookMapper.selectOne(
                new LambdaQueryWrapper<Book>().eq(Book::getDoubanId, doubanId)
        );

        BookDTO bookDTO;

        if (book == null) {
            // Fetch from Douban and save to database
            bookDTO = doubanCrawlerService.getBookDetail(doubanId);

            // 如果详情页缺少作者/出版社信息，尝试从搜索结果补充
            if ((bookDTO.getAuthor() == null || bookDTO.getPublisher() == null) && bookDTO.getTitle() != null) {
                try {
                    List<BookDTO> searchResults = doubanCrawlerService.searchBooks(bookDTO.getTitle());
                    for (BookDTO searchBook : searchResults) {
                        if (doubanId.equals(searchBook.getDoubanId())) {
                            if (bookDTO.getAuthor() == null) {
                                bookDTO.setAuthor(searchBook.getAuthor());
                            }
                            if (bookDTO.getPublisher() == null) {
                                bookDTO.setPublisher(searchBook.getPublisher());
                            }
                            if (bookDTO.getPublishDate() == null) {
                                bookDTO.setPublishDate(searchBook.getPublishDate());
                            }
                            break;
                        }
                    }
                } catch (Exception e) {
                    log.warn("Failed to supplement book info from search: {}", e.getMessage());
                }
            }

            book = saveBook(bookDTO);
            bookDTO.setId(book.getId());
        } else {
            bookDTO = new BookDTO();
            BeanUtils.copyProperties(book, bookDTO);
        }

        // Get quotes with limit
        List<QuoteDTO> quotes = getBookQuotes(doubanId, 0, limit);
        bookDTO.setQuotes(quotes);

        return bookDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Book saveBook(BookDTO bookDTO) {
        Book book = new Book();
        BeanUtils.copyProperties(bookDTO, book);

        // Check if book already exists
        Book existingBook = bookMapper.selectOne(
                new LambdaQueryWrapper<Book>().eq(Book::getDoubanId, book.getDoubanId())
        );

        if (existingBook != null) {
            book.setId(existingBook.getId());
            bookMapper.updateById(book);
        } else {
            bookMapper.insert(book);
        }

        return book;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<QuoteDTO> getBookQuotes(String doubanId) {
        log.info("Getting book quotes: {}", doubanId);

        // Get book from database
        Book book = bookMapper.selectOne(
                new LambdaQueryWrapper<Book>().eq(Book::getDoubanId, doubanId)
        );

        if (book == null) {
            // Fetch from Douban
            List<QuoteDTO> quoteDTOs = doubanCrawlerService.getBookQuotes(doubanId);

            // Save book first
            BookDTO bookDTO = doubanCrawlerService.getBookDetail(doubanId);
            book = saveBook(bookDTO);

            // Save quotes
            Long bookId = book.getId();
            for (QuoteDTO dto : quoteDTOs) {
                Quote quote = new Quote();
                quote.setBookId(bookId);
                quote.setContent(dto.getContent());
                quote.setPageNum(dto.getPageNum());
                quote.setLikes(dto.getLikes());
                quoteMapper.insert(quote);
                dto.setId(quote.getId());
                dto.setBookId(bookId);
            }

            return quoteDTOs;
        } else {
            // Get from database - 限制返回50条
            List<Quote> quotes = quoteMapper.selectList(
                    new LambdaQueryWrapper<Quote>()
                            .eq(Quote::getBookId, book.getId())
                            .orderByDesc(Quote::getLikes)
                            .last("LIMIT 50")
            );

            if (quotes.isEmpty()) {
                // Fetch from Douban and save
                List<QuoteDTO> quoteDTOs = doubanCrawlerService.getBookQuotes(doubanId);
                Long bookId = book.getId();

                for (QuoteDTO dto : quoteDTOs) {
                    Quote quote = new Quote();
                    quote.setBookId(bookId);
                    quote.setContent(dto.getContent());
                    quote.setPageNum(dto.getPageNum());
                    quote.setLikes(dto.getLikes());
                    quoteMapper.insert(quote);
                    dto.setId(quote.getId());
                    dto.setBookId(bookId);
                }

                return quoteDTOs;
            }

            return quotes.stream().map(quote -> {
                QuoteDTO dto = new QuoteDTO();
                BeanUtils.copyProperties(quote, dto);
                return dto;
            }).collect(Collectors.toList());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<QuoteDTO> getBookQuotes(String doubanId, int start, int limit) {
        log.info("Getting book quotes with pagination: doubanId={}, start={}, limit={}", doubanId, start, limit);

        // Get book from database
        Book book = bookMapper.selectOne(
                new LambdaQueryWrapper<Book>().eq(Book::getDoubanId, doubanId)
        );

        if (book == null) {
            // Save book first
            BookDTO bookDTO = doubanCrawlerService.getBookDetail(doubanId);
            book = saveBook(bookDTO);

            // Fetch quotes from Douban
            List<QuoteDTO> quoteDTOs = doubanCrawlerService.getBookQuotes(doubanId, start, limit);

            // Save quotes (如果有的话)
            Long bookId = book.getId();
            for (QuoteDTO dto : quoteDTOs) {
                Quote quote = new Quote();
                quote.setBookId(bookId);
                quote.setContent(dto.getContent());
                quote.setPageNum(dto.getPageNum());
                quote.setLikes(dto.getLikes());
                quoteMapper.insert(quote);
                dto.setId(quote.getId());
                dto.setBookId(bookId);
            }

            return quoteDTOs;
        } else {
            // Get from database with pagination
            List<Quote> quotes = quoteMapper.selectList(
                    new LambdaQueryWrapper<Quote>()
                            .eq(Quote::getBookId, book.getId())
                            .orderByDesc(Quote::getLikes)
                            .last("LIMIT " + limit + " OFFSET " + start)
            );

            if (quotes.isEmpty() && start == 0) {
                // Fetch from Douban and save
                List<QuoteDTO> quoteDTOs = new ArrayList<>();
                try {
                    quoteDTOs = doubanCrawlerService.getBookQuotes(doubanId, start, limit);
                    Long bookId = book.getId();

                    for (QuoteDTO dto : quoteDTOs) {
                        Quote quote = new Quote();
                        quote.setBookId(bookId);
                        quote.setContent(dto.getContent());
                        quote.setPageNum(dto.getPageNum());
                        quote.setLikes(dto.getLikes());
                        quoteMapper.insert(quote);
                        dto.setId(quote.getId());
                        dto.setBookId(bookId);
                    }
                } catch (Exception e) {
                    log.warn("Failed to fetch quotes for book {}: {}", doubanId, e.getMessage());
                    // 返回空列表，不抛出异常
                }

                return quoteDTOs;
            }

            return quotes.stream().map(quote -> {
                QuoteDTO dto = new QuoteDTO();
                BeanUtils.copyProperties(quote, dto);
                return dto;
            }).collect(Collectors.toList());
        }
    }
}
