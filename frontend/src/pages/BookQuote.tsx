import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Input,
  Button,
  message,
  Spin,
  Checkbox,
  Select,
  Pagination,
  Tabs,
  Card
} from 'antd';
import { SearchOutlined, CopyOutlined, HeartFilled, ArrowLeftOutlined, EditOutlined } from '@ant-design/icons';

const { TextArea } = Input;
import { searchBooks, getBookWithQuotes, getTemplates } from '../services/api';
import './BookQuote.css';

interface Book {
  id?: number;
  doubanId: string;
  title: string;
  author?: string;
  coverUrl?: string;
  publisher?: string;
  publishDate?: string;
  summary?: string;
  quotes?: Quote[];
}

interface Quote {
  id?: number;
  bookId?: number;
  content: string;
  pageNum?: number;
  likes?: number;
}

interface Template {
  id: number;
  name: string;
  platform: string;
  config: string;
}

const BookQuote = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [keyword, setKeyword] = useState('');
  const [searchResults, setSearchResults] = useState<Book[]>([]);
  const [selectedBook, setSelectedBook] = useState<Book | null>(null);
  const [selectedQuotes, setSelectedQuotes] = useState<number[]>([]);
  const [templates, setTemplates] = useState<Template[]>([]);
  const [selectedTemplate, setSelectedTemplate] = useState<number | undefined>();
  const [quoteEmoji, setQuoteEmoji] = useState('💭'); // 默认表情符号
  const [quoteLimit, setQuoteLimit] = useState(50); // 爬取摘抄条数，默认50
  const [showQuoteLimitSelector, setShowQuoteLimitSelector] = useState(false); // 是否显示摘抄条数选择器
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(20);

  // 纯文本排版相关状态
  const [activeTab, setActiveTab] = useState<string>('search'); // 'search' 或 'text'
  const [plainText, setPlainText] = useState('');
  const [bookTitle, setBookTitle] = useState('');
  const [bookAuthor, setBookAuthor] = useState('');
  const [parsedQuotes, setParsedQuotes] = useState<string[]>([]);
  const [selectedPlainQuotes, setSelectedPlainQuotes] = useState<number[]>([]);

  // 图片代理函数 - 解决豆瓣防盗链问题
  const getProxiedImageUrl = (url: string) => {
    if (!url) return '';
    // 使用后端代理接口
    return `/api/book/proxy-image?url=${encodeURIComponent(url)}`;
  };

  useEffect(() => {
    loadTemplates();
  }, []);

  const loadTemplates = async () => {
    try {
      const data = await getTemplates();
      setTemplates(data);
    } catch (error) {
      console.error('Failed to load templates', error);
    }
  };

  const handleSearch = async () => {
    if (!keyword.trim()) {
      message.warning('请输入书名');
      return;
    }

    setLoading(true);
    setSearchResults([]);
    setSelectedBook(null);

    try {
      const books = await searchBooks(keyword);
      if (books.length === 0) {
        message.info('未找到相关书籍，请尝试其他关键词');
      } else {
        setSearchResults(books);
        message.success(`找到 ${books.length} 本相关书籍`);
      }
    } catch (error: any) {
      message.error(error.message || '搜索失败，请稍后重试');
    } finally {
      setLoading(false);
    }
  };

  const handleSelectBook = async (book: Book) => {
    if (!book.doubanId) {
      message.error('无效的书籍ID');
      return;
    }

    // 先显示书籍信息和摘抄条数选择器，不立即加载摘抄
    setSelectedBook({
      ...book,
      quotes: undefined // 先不加载摘抄
    });
    setSearchResults([]);
    setShowQuoteLimitSelector(true);
  };

  const handleLoadQuotes = async () => {
    if (!selectedBook?.doubanId) {
      message.error('无效的书籍ID');
      return;
    }

    setLoading(true);
    try {
      const fullBook = await getBookWithQuotes(selectedBook.doubanId, quoteLimit);
      console.log('获取的书籍数据:', fullBook);
      console.log('摘抄数量:', fullBook?.quotes?.length);

      setSelectedBook(fullBook);
      setShowQuoteLimitSelector(false);
      setCurrentPage(1); // 重置页码
      setSelectedQuotes([]); // 清空选中

      // 根据摘抄数量显示不同的提示
      if (!fullBook.quotes || fullBook.quotes.length === 0) {
        message.info('该书籍暂无摘抄，您可以使用纯文本排版功能');
      } else {
        message.success(`加载成功！共找到 ${fullBook.quotes.length} 条摘抄`);
      }
    } catch (error: any) {
      console.error('加载错误:', error);
      // 更友好的错误提示
      if (error.message?.includes('404') || error.message?.includes('暂无')) {
        setSelectedBook({
          ...selectedBook,
          quotes: [] // 设置为空数组，显示无摘抄提示
        });
        setShowQuoteLimitSelector(false);
        message.info('该书籍暂无摘抄数据');
      } else {
        message.error(error.message || '加载失败，请稍后重试');
      }
    } finally {
      setLoading(false);
    }
  };

  const handlePageChange = (page: number, newPageSize?: number) => {
    setCurrentPage(page);
    if (newPageSize && newPageSize !== pageSize) {
      setPageSize(newPageSize);
    }
    setSelectedQuotes([]); // 切换页面时清空选中
  };

  const handleQuoteSelection = (quoteIndex: number, checked: boolean) => {
    if (checked) {
      setSelectedQuotes([...selectedQuotes, quoteIndex]);
    } else {
      setSelectedQuotes(selectedQuotes.filter(i => i !== quoteIndex));
    }
  };

  const handleSelectAllQuotes = (checked: boolean) => {
    if (checked && selectedBook?.quotes) {
      setSelectedQuotes(selectedBook.quotes.map((_, index) => index));
    } else {
      setSelectedQuotes([]);
    }
  };

  const handleBackToSearch = () => {
    setSelectedBook(null);
    setSelectedQuotes([]);
    setShowQuoteLimitSelector(false);
  };

  // 解析纯文本摘抄
  const handleParsePlainText = () => {
    if (!plainText.trim()) {
      message.warning('请输入要排版的文本');
      return;
    }

    // 按行分割，过滤空行
    const lines = plainText
      .split('\n')
      .map(line => line.trim())
      .filter(line => line.length > 0);

    if (lines.length === 0) {
      message.warning('没有找到有效的文本内容');
      return;
    }

    setParsedQuotes(lines);
    setSelectedPlainQuotes(lines.map((_, index) => index)); // 默认全选
    message.success(`成功解析 ${lines.length} 条摘抄`);
  };

  // 纯文本排版复制
  const handleCopyPlainText = () => {
    if (selectedPlainQuotes.length === 0) {
      message.warning('请至少选择一条摘抄');
      return;
    }

    if (!selectedTemplate) {
      message.warning('请选择排版模板');
      return;
    }

    const selectedContents = selectedPlainQuotes.map(i => parsedQuotes[i]).filter(Boolean);

    const template = templates.find(t => t.id === selectedTemplate);
    if (!template) {
      message.error('模板不存在');
      return;
    }

    const formatted = formatWithTemplate(
      template,
      bookTitle || '未命名书籍',
      bookAuthor || '',
      undefined,
      undefined,
      selectedContents
    );

    navigator.clipboard.writeText(formatted).then(() => {
      message.success('已复制到剪贴板！');
    }).catch(() => {
      message.error('复制失败');
    });
  };

  // 切换纯文本摘抄选择
  const handlePlainQuoteSelection = (index: number, checked: boolean) => {
    if (checked) {
      setSelectedPlainQuotes([...selectedPlainQuotes, index]);
    } else {
      setSelectedPlainQuotes(selectedPlainQuotes.filter(i => i !== index));
    }
  };

  // 全选/取消全选纯文本摘抄
  const handleSelectAllPlainQuotes = (checked: boolean) => {
    if (checked) {
      setSelectedPlainQuotes(parsedQuotes.map((_, index) => index));
    } else {
      setSelectedPlainQuotes([]);
    }
  };

  // 通用格式化函数
  const formatWithTemplate = (
    template: Template,
    title: string,
    author: string,
    rating: string | undefined,
    coverUrl: string | undefined,
    quoteContents: string[]
  ): string => {
    let formatted = '';

    // 解析模板配置
    let config: any = {};
    try {
      config = typeof template.config === 'string' ? JSON.parse(template.config) : template.config;
    } catch (e) {
      console.error('模板配置解析失败', e);
    }

    if (template.platform === 'xiaohongshu') {
      // 小红书排版：emoji + 简洁风格
      formatted = `📖《${title}》\n`;
      if (author) {
        formatted += `✍️ 作者：${author}\n`;
      }
      if (rating) {
        formatted += `⭐ 豆瓣评分：${rating}\n`;
      }
      formatted += `\n`;

      // 摘抄内容 - 使用用户选择的表情符号，并换行显示
      formatted += quoteContents.map((quote) =>
        `${quoteEmoji}\n${quote}`
      ).join('\n\n');
    } else {
      // 公众号排版 - 支持配置化
      // 1. Header
      if (config.header) {
        formatted += config.header;
      }

      // 2. Title
      if (config.titleFormat) {
        let titleStr = config.titleFormat
          .replace('{title}', title)
          .replace('{author}', author || '');
        formatted += titleStr;
      } else {
        formatted += `《${title}》\n\n`;
        if (coverUrl) {
          formatted += `![书籍封面](${coverUrl})\n\n`;
        }
      }

      // 3. Header End (用于卡片风格)
      if (config.headerEnd) {
        formatted += config.headerEnd;
      }

      // 4. Author & Rating (如果没有在 titleFormat 中)
      if (!config.titleFormat && author) {
        formatted += `📝 作者：${author}\n`;
      }
      if (!config.titleFormat && rating) {
        formatted += `⭐ 豆瓣评分：${rating}\n`;
      }
      if (!config.titleFormat && (author || rating)) {
        formatted += `\n`;
      }

      // 5. Divider
      if (config.divider) {
        formatted += config.divider;
      }

      // 6. Quotes
      if (config.quoteFormat) {
        // 支持自定义格式（如数字序号）
        formatted += quoteContents.map((quote, index) => {
          return config.quoteFormat
            .replace('{index}', String(index + 1).padStart(2, '0'))
            .replace('{content}', quote);
        }).join(config.quoteSeparator || '\n\n');
      } else {
        // 默认格式
        const prefix = config.quotePrefix || '';
        formatted += quoteContents.map(quote => prefix + quote).join(config.quoteSeparator || '\n\n');
      }

      // 7. Footer
      if (config.footer) {
        formatted += config.footer.replace('{title}', title);
      } else {
        formatted += `\n\n————————\n摘自《${title}》`;
      }
    }

    return formatted;
  };

  const formatContent = () => {
    if (!selectedBook || selectedQuotes.length === 0) {
      message.warning('请至少选择一条摘抄');
      return;
    }

    if (!selectedTemplate) {
      message.warning('请选择排版模板');
      return;
    }

    const quotes = selectedBook.quotes || [];
    const selectedQuoteContents = selectedQuotes.map(i => quotes[i]?.content).filter(Boolean);

    const template = templates.find(t => t.id === selectedTemplate);
    if (!template) {
      message.error('模板不存在');
      return;
    }

    const formatted = formatWithTemplate(
      template,
      selectedBook.title,
      selectedBook.author || '',
      selectedBook.rating,
      selectedBook.coverUrl,
      selectedQuoteContents
    );

    navigator.clipboard.writeText(formatted).then(() => {
      message.success('已复制到剪贴板！');
    }).catch(() => {
      message.error('复制失败');
    });
  };

  return (
    <div className="book-quote-container">
      <div className="content-wrapper">
        {/* Hero Section */}
        <div className="hero-section">
          {/* 返回首页按钮 */}
          <Button
            icon={<ArrowLeftOutlined />}
            onClick={() => navigate('/')}
            className="home-back-button"
          >
            返回首页
          </Button>

          <div className="hero-badge">书摘工具</div>
          <h1 className="hero-title">
            收集你的
            <span className="highlight">阅读</span>
          </h1>
          <p className="hero-subtitle">
            搜索书籍，收集打动你的句子
          </p>

          {/* 功能切换标签 */}
          <Tabs
            activeKey={activeTab}
            onChange={setActiveTab}
            centered
            className="function-tabs"
            items={[
              {
                key: 'search',
                label: (
                  <span>
                    <SearchOutlined /> 搜索书籍
                  </span>
                ),
              },
              {
                key: 'text',
                label: (
                  <span>
                    <EditOutlined /> 纯文本排版
                  </span>
                ),
              },
            ]}
          />

          {/* Search Bar */}
          {activeTab === 'search' && (
            <div className="search-container">
            <div className="search-wrapper">
              <Input
                size="large"
                placeholder="输入书名搜索，如：活着、三体、百年孤独"
                value={keyword}
                onChange={(e) => setKeyword(e.target.value)}
                onPressEnter={handleSearch}
                className="search-input"
                prefix={<SearchOutlined className="search-icon" />}
              />
              <Button
                type="primary"
                size="large"
                loading={loading}
                onClick={handleSearch}
                className="search-button"
              >
                搜索
              </Button>
            </div>
          </div>
          )}

          {/* 纯文本排版输入区 */}
          {activeTab === 'text' && (
            <div className="plain-text-container">
              <Card className="plain-text-card">
                <div className="plain-text-form">
                  <Input
                    placeholder="书名（选填）"
                    value={bookTitle}
                    onChange={(e) => setBookTitle(e.target.value)}
                    className="book-input"
                    size="large"
                  />
                  <Input
                    placeholder="作者（选填）"
                    value={bookAuthor}
                    onChange={(e) => setBookAuthor(e.target.value)}
                    className="book-input"
                    size="large"
                  />
                  <TextArea
                    placeholder="粘贴你的摘抄文本，每行一条摘抄&#10;&#10;例如：&#10;生活不能等待别人来安排，要自己去争取和奋斗。&#10;人只要不失去方向，就不会失去自己。&#10;道路并不平坦，但我们可以一起走。"
                    value={plainText}
                    onChange={(e) => setPlainText(e.target.value)}
                    rows={10}
                    className="plain-text-area"
                  />
                  <Button
                    type="primary"
                    size="large"
                    icon={<EditOutlined />}
                    onClick={handleParsePlainText}
                    className="parse-button"
                  >
                    解析摘抄
                  </Button>
                </div>
              </Card>
            </div>
          )}
        </div>

        {/* Loading State */}
        {loading && (
          <div className="loading-container">
            <Spin size="large" />
            <p className="loading-text">正在加载...</p>
          </div>
        )}

        {/* 纯文本摘抄显示区 */}
        {parsedQuotes.length > 0 && activeTab === 'text' && (
          <div className="plain-quotes-section">
            <Card className="plain-quotes-card">
              <div className="quotes-header">
                <h3 className="quotes-title">已解析摘抄</h3>
                <div className="quotes-controls">
                  <Checkbox
                    checked={selectedPlainQuotes.length === parsedQuotes.length}
                    onChange={(e) => handleSelectAllPlainQuotes(e.target.checked)}
                    className="select-all-checkbox"
                  >
                    全选 ({parsedQuotes.length})
                  </Checkbox>
                  <Select
                    placeholder="选择模板"
                    style={{ width: 160 }}
                    value={selectedTemplate}
                    onChange={setSelectedTemplate}
                    className="template-select"
                  >
                    {templates.map(t => (
                      <Select.Option key={t.id} value={t.id}>
                        {t.name}
                      </Select.Option>
                    ))}
                  </Select>
                  {selectedTemplate && templates.find(t => t.id === selectedTemplate)?.platform === 'xiaohongshu' && (
                    <Select
                      value={quoteEmoji}
                      onChange={setQuoteEmoji}
                      style={{ width: 100 }}
                      className="emoji-select"
                    >
                      <Select.Option value="💭">💭 气泡</Select.Option>
                      <Select.Option value="✨">✨ 星光</Select.Option>
                      <Select.Option value="📝">📝 笔记</Select.Option>
                      <Select.Option value="💡">💡 灯泡</Select.Option>
                      <Select.Option value="🌟">🌟 星星</Select.Option>
                      <Select.Option value="💫">💫 流星</Select.Option>
                      <Select.Option value="🎯">🎯 靶心</Select.Option>
                      <Select.Option value="🔖">🔖 书签</Select.Option>
                      <Select.Option value="📌">📌 图钉</Select.Option>
                      <Select.Option value="🌸">🌸 樱花</Select.Option>
                    </Select>
                  )}
                  <Button
                    type="primary"
                    icon={<CopyOutlined />}
                    disabled={selectedPlainQuotes.length === 0}
                    onClick={handleCopyPlainText}
                    className="copy-button"
                  >
                    复制选中 ({selectedPlainQuotes.length})
                  </Button>
                </div>
              </div>

              <div className="quotes-list">
                {parsedQuotes.map((quote, index) => (
                  <div
                    key={index}
                    className={`quote-item ${selectedPlainQuotes.includes(index) ? 'selected' : ''}`}
                  >
                    <Checkbox
                      checked={selectedPlainQuotes.includes(index)}
                      onChange={(e) => handlePlainQuoteSelection(index, e.target.checked)}
                      className="quote-checkbox"
                    />
                    <div className="quote-content">
                      <p className="quote-text">"{quote}"</p>
                    </div>
                  </div>
                ))}
              </div>
            </Card>
          </div>
        )}

        {/* Search Results */}
        {searchResults.length > 0 && (
          <div className="results-section">
            <h2 className="section-title">搜索结果</h2>
            <div className="books-grid">
              {searchResults.map((book, index) => (
                <div
                  key={index}
                  className="book-card"
                  onClick={() => handleSelectBook(book)}
                >
                  {book.coverUrl && (
                    <div className="book-cover">
                      <img src={getProxiedImageUrl(book.coverUrl)} alt={book.title} />
                      <div className="book-overlay">
                        <span>查看详情</span>
                      </div>
                    </div>
                  )}
                  <div className="book-info">
                    <h3 className="book-title">{book.title}</h3>
                    <p className="book-author">{book.author}</p>
                    <p className="book-meta">
                      {book.publisher} · {book.publishDate}
                    </p>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Book Detail */}
        {selectedBook && (
          <div className="book-detail-section">
            {/* 返回按钮 */}
            <Button
              icon={<ArrowLeftOutlined />}
              onClick={handleBackToSearch}
              className="back-button"
            >
              返回搜索
            </Button>

            <div className="book-detail-header">
              {selectedBook.coverUrl && (
                <div className="detail-cover">
                  <img src={getProxiedImageUrl(selectedBook.coverUrl)} alt={selectedBook.title} />
                </div>
              )}
              <div className="detail-info">
                <h2 className="detail-title">{selectedBook.title}</h2>
                {selectedBook.rating && (
                  <div className="detail-rating">
                    <span className="rating-score">⭐ {selectedBook.rating}</span>
                    <span className="rating-label">豆瓣评分</span>
                  </div>
                )}
                <p className="detail-author">作者：{selectedBook.author || '未知'}</p>
                {selectedBook.publisher && (
                  <p className="detail-publisher">
                    {selectedBook.publisher} · {selectedBook.publishDate}
                  </p>
                )}
                {selectedBook.summary && (
                  <p className="detail-summary">{selectedBook.summary}</p>
                )}
              </div>
            </div>

            {/* 摘抄条数选择器 - 显示在书籍详情之后 */}
            {showQuoteLimitSelector && (
              <div className="quote-limit-panel">
                <h3 className="panel-title">选择要爬取的摘抄条数</h3>
                <p className="panel-subtitle">摘抄数量越多，加载时间越长</p>
                <div className="quote-limit-options">
                  <Select
                    value={quoteLimit}
                    onChange={setQuoteLimit}
                    style={{ width: 120 }}
                    size="large"
                  >
                    <Select.Option value={20}>20条</Select.Option>
                    <Select.Option value={30}>30条</Select.Option>
                    <Select.Option value={50}>50条</Select.Option>
                    <Select.Option value={80}>80条</Select.Option>
                    <Select.Option value={100}>100条</Select.Option>
                  </Select>
                  <Button
                    type="primary"
                    size="large"
                    loading={loading}
                    onClick={handleLoadQuotes}
                    className="load-quotes-button"
                  >
                    开始加载摘抄
                  </Button>
                </div>
              </div>
            )}

            {/* Quotes Section */}
            {selectedBook.quotes && selectedBook.quotes.length > 0 ? (
              <div className="quotes-section">
                <div className="quotes-header">
                  <h3 className="quotes-title">精彩摘抄</h3>
                  <div className="quotes-controls">
                    <Checkbox
                      checked={selectedQuotes.length === selectedBook.quotes.length}
                      onChange={(e) => handleSelectAllQuotes(e.target.checked)}
                      className="select-all-checkbox"
                    >
                      全选 ({selectedBook.quotes.length})
                    </Checkbox>
                    <Select
                      placeholder="选择模板"
                      style={{ width: 160 }}
                      value={selectedTemplate}
                      onChange={setSelectedTemplate}
                      className="template-select"
                    >
                      {templates.map(t => (
                        <Select.Option key={t.id} value={t.id}>
                          {t.name}
                        </Select.Option>
                      ))}
                    </Select>
                    {selectedTemplate && templates.find(t => t.id === selectedTemplate)?.platform === 'xiaohongshu' && (
                      <Select
                        value={quoteEmoji}
                        onChange={setQuoteEmoji}
                        style={{ width: 100 }}
                        className="emoji-select"
                      >
                        <Select.Option value="💭">💭 气泡</Select.Option>
                        <Select.Option value="✨">✨ 星光</Select.Option>
                        <Select.Option value="📝">📝 笔记</Select.Option>
                        <Select.Option value="💡">💡 灯泡</Select.Option>
                        <Select.Option value="🌟">🌟 星星</Select.Option>
                        <Select.Option value="💫">💫 流星</Select.Option>
                        <Select.Option value="🎯">🎯 靶心</Select.Option>
                        <Select.Option value="🔖">🔖 书签</Select.Option>
                        <Select.Option value="📌">📌 图钉</Select.Option>
                        <Select.Option value="🌸">🌸 樱花</Select.Option>
                      </Select>
                    )}
                    <Button
                      type="primary"
                      icon={<CopyOutlined />}
                      disabled={selectedQuotes.length === 0}
                      onClick={formatContent}
                      className="copy-button"
                    >
                      复制选中 ({selectedQuotes.length})
                    </Button>
                  </div>
                </div>

                <div className="quotes-list">
                  {selectedBook.quotes
                    .slice((currentPage - 1) * pageSize, currentPage * pageSize)
                    .map((quote, index) => {
                      const absoluteIndex = (currentPage - 1) * pageSize + index;
                      return (
                        <div
                          key={absoluteIndex}
                          className={`quote-item ${selectedQuotes.includes(absoluteIndex) ? 'selected' : ''}`}
                        >
                          <Checkbox
                            checked={selectedQuotes.includes(absoluteIndex)}
                            onChange={(e) => handleQuoteSelection(absoluteIndex, e.target.checked)}
                            className="quote-checkbox"
                          />
                          <div className="quote-content">
                            <p className="quote-text">"{quote.content}"</p>
                            {quote.likes && quote.likes > 0 && (
                              <div className="quote-likes">
                                <HeartFilled /> {quote.likes}
                              </div>
                            )}
                          </div>
                        </div>
                      );
                    })}
                </div>

                {selectedBook.quotes.length > pageSize && (
                  <div className="quotes-pagination">
                    <Pagination
                      current={currentPage}
                      pageSize={pageSize}
                      total={selectedBook.quotes.length}
                      onChange={handlePageChange}
                      showSizeChanger
                      showTotal={(total) => `共 ${total} 条摘抄`}
                      pageSizeOptions={['10', '20', '50', '100']}
                    />
                  </div>
                )}
              </div>
            ) : (
              <div className="no-quotes">
                <div className="no-quotes-icon">📖</div>
                <h3 className="no-quotes-title">该书籍暂无摘抄</h3>
                <p className="no-quotes-text">
                  豆瓣上还没有读者为这本书添加摘抄。
                  <br />
                  您可以切换到「纯文本排版」功能，粘贴您自己的摘抄进行排版。
                </p>
                <Button
                  type="primary"
                  onClick={() => setActiveTab('text')}
                  className="switch-tab-button"
                >
                  去纯文本排版
                </Button>
              </div>
            )}
          </div>
        )}

        {/* Instructions - 仅在搜索模式下显示 */}
        {activeTab === 'search' && !selectedBook && searchResults.length === 0 && !loading && (
          <div className="instructions-section">
            <div className="instruction-card">
              <div className="instruction-number">01</div>
              <h3>搜索书籍</h3>
              <p>输入书名，找到你想要的书</p>
            </div>
            <div className="instruction-card">
              <div className="instruction-number">02</div>
              <h3>选择摘抄</h3>
              <p>勾选喜欢的句子，支持批量选择</p>
            </div>
            <div className="instruction-card">
              <div className="instruction-number">03</div>
              <h3>一键复制</h3>
              <p>点击复制按钮，方便分享保存</p>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default BookQuote;
