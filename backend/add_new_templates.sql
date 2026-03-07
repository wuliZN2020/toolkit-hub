-- 添加新的公众号排版模板

-- 1. 文艺清新风
INSERT INTO template (name, platform, config, sort_order, deleted, create_time, update_time)
VALUES (
    '文艺清新风',
    'wechat',
    '{
        "header": "◆ 今日书摘\\n\\n",
        "titleFormat": "《{title}》\\n[{author}]\\n\\n",
        "divider": "────────────\\n\\n",
        "quotePrefix": "✦ ",
        "quoteSeparator": "\\n\\n",
        "footer": "\\n────────────\\n📖 阅读让生活更美好"
    }',
    2,
    0,
    NOW(),
    NOW()
);

-- 2. 数字序号风
INSERT INTO template (name, platform, config, sort_order, deleted, create_time, update_time)
VALUES (
    '数字序号风',
    'wechat',
    '{
        "titleFormat": "📚《{title}》{author}\\n\\n",
        "divider": "━━━━━━━━━━━━━━\\n\\n",
        "quoteFormat": "{index}.\\n{content}",
        "quoteSeparator": "\\n\\n",
        "footer": "\\n━━━━━━━━━━━━━━\\n💭 用阅读对抗虚无"
    }',
    3,
    0,
    NOW(),
    NOW()
);

-- 3. 极简引用风
INSERT INTO template (name, platform, config, sort_order, deleted, create_time, update_time)
VALUES (
    '极简引用风',
    'wechat',
    '{
        "titleFormat": "《{title}》{author}\\n\\n",
        "divider": "——————\\n\\n",
        "quoteFormat": "\\" {content} \\"",
        "quoteSeparator": "\\n\\n",
        "footer": "\\n——————\\n\\n长按复制 | 分享收藏"
    }',
    4,
    0,
    NOW(),
    NOW()
);
