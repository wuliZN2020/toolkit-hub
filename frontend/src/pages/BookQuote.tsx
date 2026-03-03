import { useState } from 'react';
import { Card, Input, Button, Typography, Space, message } from 'antd';
import { SearchOutlined } from '@ant-design/icons';

const { Title, Paragraph } = Typography;

const BookQuote = () => {
  const [loading, setLoading] = useState(false);
  const [keyword, setKeyword] = useState('');

  const handleSearch = async () => {
    if (!keyword.trim()) {
      message.warning('Please enter Douban book URL or ID');
      return;
    }

    setLoading(true);
    try {
      // TODO: Call API to search book
      message.info('This feature is under development');
    } catch (error) {
      message.error('Search failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ padding: '40px 20px', maxWidth: 1200, margin: '0 auto' }}>
      <Card>
        <Title level={2}>Book Quote Formatter</Title>
        <Paragraph style={{ color: '#666', marginBottom: 32 }}>
          Enter Douban book URL or ID to crawl quotes and format them for different platforms
        </Paragraph>

        <Space.Compact style={{ width: '100%', maxWidth: 600 }}>
          <Input
            size="large"
            placeholder="Enter Douban book URL or ID (e.g., https://book.douban.com/subject/xxxxx/)"
            value={keyword}
            onChange={(e) => setKeyword(e.target.value)}
            onPressEnter={handleSearch}
          />
          <Button
            type="primary"
            size="large"
            icon={<SearchOutlined />}
            loading={loading}
            onClick={handleSearch}
          >
            Search
          </Button>
        </Space.Compact>

        <div style={{ marginTop: 40, padding: 24, background: '#f5f5f5', borderRadius: 8 }}>
          <Paragraph strong>How to use:</Paragraph>
          <ol style={{ marginLeft: 20 }}>
            <li>Go to Douban book page (e.g., https://book.douban.com/subject/1234567/)</li>
            <li>Copy the book URL or ID</li>
            <li>Paste it here and click Search</li>
            <li>Select quotes and choose a template</li>
            <li>Export formatted content for WeChat or Xiaohongshu</li>
          </ol>
        </div>
      </Card>
    </div>
  );
};

export default BookQuote;
