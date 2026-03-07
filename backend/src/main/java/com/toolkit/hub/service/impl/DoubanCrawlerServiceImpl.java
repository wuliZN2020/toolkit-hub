package com.toolkit.hub.service.impl;

import com.toolkit.hub.common.exception.BusinessException;
import com.toolkit.hub.dto.BookDTO;
import com.toolkit.hub.dto.QuoteDTO;
import com.toolkit.hub.service.DoubanCrawlerService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Douban crawler service implementation with anti-anti-crawler strategies
 */
@Slf4j
@Service
public class DoubanCrawlerServiceImpl implements DoubanCrawlerService {

    private final OkHttpClient httpClient;
    private final Random random;
    private long lastRequestTime = 0;
    private static final long MIN_REQUEST_INTERVAL = 1000; // 最小请求间隔1秒

    // User-Agent 池
    private static final String[] USER_AGENTS = {
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.1 Safari/605.1.15",
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:121.0) Gecko/20100101 Firefox/121.0",
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36 Edg/119.0.0.0"
    };

    public DoubanCrawlerServiceImpl() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .followRedirects(true)  // 跟随重定向
                .followSslRedirects(true)
                .retryOnConnectionFailure(true)  // 连接失败时重试
                .build();
        this.random = new Random();
    }

    @Override
    public List<BookDTO> searchBooks(String keyword) {
        List<BookDTO> books = new ArrayList<>();

        try {
            // 使用豆瓣搜索建议API
            String url = "https://www.douban.com/j/search_suggest?q=" +
                    java.net.URLEncoder.encode(keyword, "UTF-8");
            String jsonResponse = fetchHtml(url);

            log.debug("Search API response: {}", jsonResponse);

            // 解析JSON响应
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode rootNode = mapper.readTree(jsonResponse);
            com.fasterxml.jackson.databind.JsonNode cardsNode = rootNode.get("cards");

            if (cardsNode != null && cardsNode.isArray()) {
                for (com.fasterxml.jackson.databind.JsonNode card : cardsNode) {
                    String type = card.get("type").asText();

                    // 只处理书籍类型
                    if ("book".equals(type)) {
                        BookDTO book = new BookDTO();

                        // 从URL提取豆瓣ID
                        String urlStr = card.get("url").asText();
                        book.setDoubanId(extractDoubanId(urlStr));

                        // 提取其他信息
                        book.setTitle(card.get("title").asText());
                        book.setCoverUrl(card.get("cover_url").asText());

                        // 解析subtitle: "9.4分 / 余华 / 2012 / 作家出版社"
                        String subtitle = card.get("card_subtitle").asText();
                        String[] parts = subtitle.split(" / ");
                        if (parts.length >= 2) {
                            book.setAuthor(parts[1].trim());
                        }
                        if (parts.length >= 3) {
                            book.setPublishDate(parts[2].trim());
                        }
                        if (parts.length >= 4) {
                            book.setPublisher(parts[3].trim());
                        }

                        books.add(book);
                        log.info("Found book: {} (ID: {})", book.getTitle(), book.getDoubanId());
                    }
                }
            }

            if (books.isEmpty()) {
                log.warn("No books found for keyword: {}", keyword);
            }

        } catch (Exception e) {
            log.error("Error searching books: {}", keyword, e);
            throw new BusinessException("搜索书籍失败: " + e.getMessage());
        }

        return books;
    }

    @Override
    public BookDTO getBookDetail(String doubanId) {
        try {
            String url = "https://book.douban.com/subject/" + doubanId + "/";
            String html = fetchHtml(url);
            Document doc = Jsoup.parse(html);

            log.debug("Fetched HTML length: {}", html.length());

            BookDTO book = new BookDTO();
            book.setDoubanId(doubanId);

            // Extract title
            Element titleElement = doc.selectFirst("h1 span");
            if (titleElement != null) {
                String title = titleElement.text().trim();
                book.setTitle(title);
                log.debug("Extracted title: {}", title);
            } else {
                log.warn("Title element not found for doubanId: {}", doubanId);
                // 尝试其他选择器
                titleElement = doc.selectFirst("h1");
                if (titleElement != null) {
                    String title = titleElement.text().trim();
                    book.setTitle(title);
                    log.debug("Extracted title from h1: {}", title);
                }
            }

            // 如果还是没有 title，抛出异常
            if (book.getTitle() == null || book.getTitle().isEmpty()) {
                log.error("Failed to extract title. HTML snippet: {}", html.substring(0, Math.min(500, html.length())));
                throw new BusinessException("无法获取书籍标题，可能被豆瓣反爬虫拦截");
            }

            // Extract cover
            Element imgElement = doc.selectFirst("#mainpic img");
            if (imgElement != null) {
                book.setCoverUrl(imgElement.attr("src"));
            }

            // Extract rating
            Element ratingElement = doc.selectFirst(".rating_num");
            if (ratingElement != null) {
                book.setRating(ratingElement.text().trim());
                log.debug("Extracted rating: {}", book.getRating());
            }

            // Extract book info - 使用更健壮的方法
            Element infoElement = doc.selectFirst("#info");
            if (infoElement != null) {
                String infoHtml = infoElement.html();

                // 提取作者
                String author = extractInfoValueFromHtml(infoHtml, "作者");
                if (author == null || author.isEmpty()) {
                    author = extractInfoValueFromHtml(infoHtml, "作\\s*者");
                }
                book.setAuthor(author);

                // 提取出版社
                String publisher = extractInfoValueFromHtml(infoHtml, "出版社");
                book.setPublisher(publisher);

                // 提取出版年
                String publishDate = extractInfoValueFromHtml(infoHtml, "出版年");
                book.setPublishDate(publishDate);

                // 提取ISBN
                String isbn = extractInfoValueFromHtml(infoHtml, "ISBN");
                book.setIsbn(isbn);

                log.debug("Extracted info - Author: {}, Publisher: {}, Date: {}, ISBN: {}",
                        author, publisher, publishDate, isbn);
            }

            // Extract summary
            Elements summaryElements = doc.select("#link-report .intro");
            if (!summaryElements.isEmpty()) {
                Element lastIntro = summaryElements.last();
                if (lastIntro != null) {
                    book.setSummary(lastIntro.text().trim());
                }
            }

            log.info("Successfully extracted book: {}", book.getTitle());
            return book;

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error getting book detail: {}", doubanId, e);
            throw new BusinessException("获取书籍详情失败: " + e.getMessage());
        }
    }

    @Override
    public List<QuoteDTO> getBookQuotes(String doubanId) {
        // 默认获取前50条
        return getBookQuotes(doubanId, 0, 50);
    }

    @Override
    public List<QuoteDTO> getBookQuotes(String doubanId, int start, int limit) {
        List<QuoteDTO> quotes = new ArrayList<>();

        try {
            // 豆瓣每页显示20条，计算需要爬取的页数
            int startPage = start / 20;
            int endPage = (start + limit - 1) / 20;
            int itemsToSkip = start % 20;
            int itemsCollected = 0;

            log.info("Fetching quotes for book {}: start={}, limit={}, pages={}-{}",
                    doubanId, start, limit, startPage, endPage);

            for (int page = startPage; page <= endPage && itemsCollected < limit; page++) {
                int pageStart = page * 20;
                String url = "https://book.douban.com/subject/" + doubanId +
                           "/blockquotes?sort=score&start=" + pageStart;

                log.debug("Fetching page {}: {}", page, url);

                String html;
                try {
                    html = fetchHtml(url);
                } catch (IOException e) {
                    // 如果是404错误，说明该书没有摘抄页面
                    if (e.getMessage() != null && e.getMessage().contains("404")) {
                        log.info("Book {} has no quotes page (404)", doubanId);
                        return quotes; // 返回空列表
                    }
                    throw e; // 其他错误继续抛出
                }

                Document doc = Jsoup.parse(html);

                Elements quoteElements = doc.select(".blockquote-list ul li");

                if (quoteElements.isEmpty()) {
                    log.info("No more quotes found on page {}, stopping", page);
                    break;
                }

                log.debug("Found {} quote elements on page {}", quoteElements.size(), page);

                for (int i = 0; i < quoteElements.size() && itemsCollected < limit; i++) {
                    // 第一页需要跳过前面的项
                    if (page == startPage && i < itemsToSkip) {
                        continue;
                    }

                    Element element = quoteElements.get(i);
                    try {
                        QuoteDTO quote = new QuoteDTO();

                        // 提取摘抄内容（在 figure 标签内）
                        Element figureElement = element.selectFirst("figure");
                        if (figureElement != null) {
                            // 克隆元素以便安全修改
                            Element contentElement = figureElement.clone();

                            // 移除所有链接（查看原文等）
                            Elements links = contentElement.select("a");
                            for (Element link : links) {
                                link.remove();
                            }

                            // 移除 blockquote-extra 部分（作者信息等）
                            Elements extras = contentElement.select(".blockquote-extra");
                            for (Element extra : extras) {
                                extra.remove();
                            }

                            String content = contentElement.text().trim();
                            // 清理多余的空格和括号
                            content = content.replaceAll("\\s*\\(\\s*\\)\\s*$", "").trim();
                            quote.setContent(content);
                        }

                        // 提取回复数（作为点赞数）
                        Element metaElement = element.selectFirst(".blockquote-meta span");
                        if (metaElement != null) {
                            String text = metaElement.text(); // 例如: "62回复"
                            quote.setLikes(extractLikesCount(text));
                        }

                        if (quote.getContent() != null && !quote.getContent().isEmpty()) {
                            quotes.add(quote);
                            itemsCollected++;
                            log.debug("Extracted quote {}: {} (likes: {})",
                                    itemsCollected,
                                    quote.getContent().substring(0, Math.min(50, quote.getContent().length())),
                                    quote.getLikes());
                        }
                    } catch (Exception e) {
                        log.error("Error parsing quote element", e);
                    }
                }
            }

            log.info("Successfully extracted {} quotes for book {} (requested: {})",
                    quotes.size(), doubanId, limit);

            // 确保按点赞数从高到低排序
            quotes.sort((q1, q2) -> {
                Integer likes1 = q1.getLikes() != null ? q1.getLikes() : 0;
                Integer likes2 = q2.getLikes() != null ? q2.getLikes() : 0;
                return likes2.compareTo(likes1); // 降序
            });

        } catch (Exception e) {
            log.error("Error getting book quotes: {}", doubanId, e);
            throw new BusinessException("获取书籍摘抄失败: " + e.getMessage());
        }

        return quotes;
    }

    /**
     * Fetch HTML content from URL with anti-anti-crawler strategies
     */
    private String fetchHtml(String url) throws IOException {
        // 请求限流：确保两次请求之间有间隔
        rateLimitDelay();

        // 随机选择 User-Agent
        String userAgent = USER_AGENTS[random.nextInt(USER_AGENTS.length)];

        Request request = new Request.Builder()
                .url(url)
                // 完整的浏览器请求头
                .addHeader("User-Agent", userAgent)
                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
                // 不手动设置 Accept-Encoding，让 OkHttp 自动处理
                .addHeader("Connection", "keep-alive")
                .addHeader("Upgrade-Insecure-Requests", "1")
                .addHeader("Cache-Control", "max-age=0")
                .addHeader("Referer", "https://www.douban.com/")
                // 添加 Cookie 模拟登录用户（可选）
                .addHeader("Cookie", "bid=" + generateBid())
                .build();

        log.debug("Fetching URL: {} with User-Agent: {}", url, userAgent);

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("Request failed with code: {}, url: {}", response.code(), url);
                throw new IOException("Unexpected response code: " + response.code());
            }

            String html = response.body() != null ? response.body().string() : "";
            log.debug("Successfully fetched HTML, length: {}", html.length());

            return html;
        }
    }

    /**
     * 请求限流：确保请求之间有合理间隔
     */
    private synchronized void rateLimitDelay() {
        long currentTime = System.currentTimeMillis();
        long timeSinceLastRequest = currentTime - lastRequestTime;

        if (timeSinceLastRequest < MIN_REQUEST_INTERVAL) {
            long sleepTime = MIN_REQUEST_INTERVAL - timeSinceLastRequest;
            // 添加随机延迟（500ms-1500ms）
            sleepTime += random.nextInt(1000) + 500;

            try {
                log.debug("Rate limiting: sleeping for {}ms", sleepTime);
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Rate limit sleep interrupted", e);
            }
        }

        lastRequestTime = System.currentTimeMillis();
    }

    /**
     * 生成随机的豆瓣 bid（浏览器标识）
     */
    private String generateBid() {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder bid = new StringBuilder();
        for (int i = 0; i < 11; i++) {
            bid.append(chars.charAt(random.nextInt(chars.length())));
        }
        return bid.toString();
    }

    /**
     * Extract Douban ID from URL
     */
    private String extractDoubanId(String url) {
        if (url == null) return null;
        String[] parts = url.split("/");
        for (int i = 0; i < parts.length; i++) {
            if ("subject".equals(parts[i]) && i + 1 < parts.length) {
                return parts[i + 1];
            }
        }
        return null;
    }

    /**
     * Extract author from info text
     */
    private String extractAuthor(String info) {
        return extractPattern(info, "作者:", "/");
    }

    /**
     * Extract publisher from info text
     */
    private String extractPublisher(String info) {
        return extractPattern(info, "出版社:", "/");
    }

    /**
     * Extract publish date from info text
     */
    private String extractPublishDate(String info) {
        return extractPattern(info, "出版年:", "/");
    }

    /**
     * Extract info value by key
     */
    private String extractInfoValue(String text, String key) {
        int startIndex = text.indexOf(key);
        if (startIndex == -1) return null;

        startIndex += key.length();
        int endIndex = text.indexOf(" ", startIndex);
        if (endIndex == -1) {
            endIndex = text.length();
        }

        return text.substring(startIndex, endIndex).trim();
    }

    /**
     * 从HTML中提取信息值（更健壮的方法）
     */
    private String extractInfoValueFromHtml(String html, String key) {
        try {
            // 查找包含key的span标签
            String pattern = "<span class=\"pl\">[^>]*?" + key + "\\s*:?\\s*</span>([^<]*)";
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
            java.util.regex.Matcher m = p.matcher(html);

            if (m.find()) {
                String value = m.group(1).trim();
                // 移除可能的HTML标签
                value = value.replaceAll("<[^>]+>", "");
                // 移除多余的空格
                value = value.replaceAll("\\s+", " ").trim();
                // 移除尾部的斜杠和冒号
                value = value.replaceAll("[:/\\s]+$", "").trim();
                return value.isEmpty() ? null : value;
            }

            // 尝试另一种模式：直接在文本中查找
            int keyIndex = html.indexOf(key + ":");
            if (keyIndex == -1) {
                keyIndex = html.indexOf(key + "</span>");
            }

            if (keyIndex != -1) {
                int startIndex = keyIndex + key.length();
                // 跳过 :  和 </span>
                while (startIndex < html.length() &&
                        (html.charAt(startIndex) == ':' ||
                                html.charAt(startIndex) == ' ' ||
                                html.charAt(startIndex) == '<')) {
                    if (html.charAt(startIndex) == '<') {
                        int endTag = html.indexOf('>', startIndex);
                        if (endTag != -1) {
                            startIndex = endTag + 1;
                        } else {
                            break;
                        }
                    } else {
                        startIndex++;
                    }
                }

                // 找到值的结束位置
                int endIndex = html.indexOf("<", startIndex);
                if (endIndex == -1) {
                    endIndex = html.indexOf("\n", startIndex);
                }
                if (endIndex == -1) {
                    endIndex = Math.min(startIndex + 100, html.length());
                }

                String value = html.substring(startIndex, endIndex).trim();
                value = value.replaceAll("<[^>]+>", "");
                value = value.replaceAll("\\s+", " ").trim();
                return value.isEmpty() ? null : value;
            }

            return null;
        } catch (Exception e) {
            log.warn("Error extracting {} from HTML", key, e);
            return null;
        }
    }

    /**
     * Extract pattern from text
     */
    private String extractPattern(String text, String start, String end) {
        int startIndex = text.indexOf(start);
        if (startIndex == -1) return null;

        startIndex += start.length();
        int endIndex = text.indexOf(end, startIndex);
        if (endIndex == -1) {
            endIndex = text.length();
        }

        return text.substring(startIndex, endIndex).trim();
    }

    /**
     * Extract likes count from text
     */
    private Integer extractLikesCount(String text) {
        if (text == null) return 0;

        try {
            String numStr = text.replaceAll("[^0-9]", "");
            return numStr.isEmpty() ? 0 : Integer.parseInt(numStr);
        } catch (Exception e) {
            return 0;
        }
    }
}
