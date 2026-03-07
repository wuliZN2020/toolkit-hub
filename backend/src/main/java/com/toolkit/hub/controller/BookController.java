package com.toolkit.hub.controller;

import com.toolkit.hub.common.result.Result;
import com.toolkit.hub.dto.BookDTO;
import com.toolkit.hub.dto.QuoteDTO;
import com.toolkit.hub.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Book Controller
 */
@Slf4j
@RestController
@RequestMapping("/book")
@RequiredArgsConstructor
@Tag(name = "Book API", description = "Book search and quote management")
public class BookController {

    private final BookService bookService;
    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    @GetMapping("/search")
    @Operation(summary = "Search books", description = "Search books by keyword from Douban")
    public Result<List<BookDTO>> searchBooks(
            @Parameter(description = "Search keyword", required = true)
            @RequestParam String keyword) {
        log.info("Search books with keyword: {}", keyword);
        List<BookDTO> books = bookService.searchBooks(keyword);
        return Result.success(books);
    }

    @GetMapping("/{doubanId}")
    @Operation(summary = "Get book detail", description = "Get book detail with quotes by Douban ID")
    public Result<BookDTO> getBookDetail(
            @Parameter(description = "Douban book ID", required = true)
            @PathVariable String doubanId,
            @Parameter(description = "Quote limit (default 50, max 100)")
            @RequestParam(defaultValue = "50") int limit) {
        // Limit max to 100
        limit = Math.min(limit, 100);
        log.info("Get book detail: {}, limit: {}", doubanId, limit);
        BookDTO book = bookService.getBookWithQuotes(doubanId, limit);
        return Result.success(book);
    }

    @GetMapping("/{doubanId}/quotes")
    @Operation(summary = "Get book quotes", description = "Get book quotes by Douban ID with pagination")
    public Result<List<QuoteDTO>> getBookQuotes(
            @Parameter(description = "Douban book ID", required = true)
            @PathVariable String doubanId,
            @Parameter(description = "Start index (default 0)")
            @RequestParam(defaultValue = "0") int start,
            @Parameter(description = "Page size (default 20, max 100)")
            @RequestParam(defaultValue = "20") int limit) {
        // Limit max to 100
        limit = Math.min(limit, 100);
        log.info("Get book quotes: doubanId={}, start={}, limit={}", doubanId, start, limit);
        List<QuoteDTO> quotes = bookService.getBookQuotes(doubanId, start, limit);
        return Result.success(quotes);
    }

    @GetMapping("/proxy-image")
    @Operation(summary = "Proxy image", description = "Proxy Douban images to bypass anti-hotlinking")
    public ResponseEntity<byte[]> proxyImage(@RequestParam String url) {
        try {
            log.debug("Proxying image: {}", url);

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36")
                    .addHeader("Referer", "https://book.douban.com/")
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    byte[] imageBytes = response.body().bytes();

                    HttpHeaders headers = new HttpHeaders();
                    String contentType = response.header("Content-Type", "image/jpeg");
                    headers.setContentType(MediaType.parseMediaType(contentType));
                    headers.setCacheControl("public, max-age=86400");

                    return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
                }
            }

            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error proxying image: {}", url, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
