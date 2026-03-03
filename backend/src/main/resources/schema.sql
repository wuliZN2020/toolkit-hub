-- Create database
CREATE DATABASE IF NOT EXISTS toolkit_hub DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE toolkit_hub;

-- Book table
CREATE TABLE IF NOT EXISTS `book` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `douban_id` VARCHAR(50) UNIQUE COMMENT 'Douban book ID',
    `title` VARCHAR(255) NOT NULL COMMENT 'Book title',
    `author` VARCHAR(255) COMMENT 'Author',
    `cover_url` VARCHAR(500) COMMENT 'Cover image URL',
    `isbn` VARCHAR(50) COMMENT 'ISBN',
    `publisher` VARCHAR(255) COMMENT 'Publisher',
    `publish_date` VARCHAR(50) COMMENT 'Publish date',
    `summary` TEXT COMMENT 'Book summary',
    `deleted` TINYINT DEFAULT 0 COMMENT 'Logical delete flag (0: not deleted, 1: deleted)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
    PRIMARY KEY (`id`),
    INDEX `idx_douban_id` (`douban_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Book information table';

-- Quote table
CREATE TABLE IF NOT EXISTS `quote` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `book_id` BIGINT NOT NULL COMMENT 'Book ID',
    `content` TEXT NOT NULL COMMENT 'Quote content',
    `page_num` INT COMMENT 'Page number',
    `likes` INT DEFAULT 0 COMMENT 'Number of likes',
    `deleted` TINYINT DEFAULT 0 COMMENT 'Logical delete flag',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
    PRIMARY KEY (`id`),
    INDEX `idx_book_id` (`book_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Book quotes table';

-- Template table
CREATE TABLE IF NOT EXISTS `template` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `name` VARCHAR(100) NOT NULL COMMENT 'Template name',
    `platform` VARCHAR(20) NOT NULL COMMENT 'Platform (wechat/xiaohongshu)',
    `config` JSON COMMENT 'Template configuration (font, color, etc.)',
    `preview_url` VARCHAR(500) COMMENT 'Preview image URL',
    `sort_order` INT DEFAULT 0 COMMENT 'Sort order',
    `deleted` TINYINT DEFAULT 0 COMMENT 'Logical delete flag',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
    PRIMARY KEY (`id`),
    INDEX `idx_platform` (`platform`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Format template table';

-- User work table
CREATE TABLE IF NOT EXISTS `user_work` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    `book_id` BIGINT COMMENT 'Book ID',
    `template_id` BIGINT COMMENT 'Template ID',
    `content` LONGTEXT COMMENT 'Formatted content',
    `platform` VARCHAR(20) COMMENT 'Target platform',
    `image_url` VARCHAR(500) COMMENT 'Generated image URL',
    `deleted` TINYINT DEFAULT 0 COMMENT 'Logical delete flag',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
    PRIMARY KEY (`id`),
    INDEX `idx_book_id` (`book_id`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='User works table';

-- Insert default templates
INSERT INTO `template` (`name`, `platform`, `config`, `sort_order`) VALUES
('简约现代', 'wechat', '{"fontSize": 16, "lineHeight": 1.8, "fontFamily": "Microsoft YaHei", "color": "#333", "quoteStyle": "border-left"}', 1),
('文艺清新', 'wechat', '{"fontSize": 15, "lineHeight": 2.0, "fontFamily": "STKaiti", "color": "#555", "quoteStyle": "quotation-marks"}', 2),
('经典商务', 'wechat', '{"fontSize": 16, "lineHeight": 1.75, "fontFamily": "STSong", "color": "#222", "quoteStyle": "background"}', 3),
('小红书风格', 'xiaohongshu', '{"fontSize": 14, "lineHeight": 1.6, "useEmoji": true, "style": "casual"}', 1),
('小红书简约', 'xiaohongshu', '{"fontSize": 15, "lineHeight": 1.5, "useEmoji": false, "style": "minimal"}', 2);
