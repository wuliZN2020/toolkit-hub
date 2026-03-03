import { Button, Card, Col, Row, Typography } from 'antd';
import { BookOutlined, ToolOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';

const { Title, Paragraph } = Typography;

const Home = () => {
  const navigate = useNavigate();

  const tools = [
    {
      key: 'book-quote',
      title: 'Book Quote Formatter',
      description: 'Crawl book quotes from Douban and format them for WeChat Official Account and Xiaohongshu',
      icon: <BookOutlined style={{ fontSize: 48, color: '#1890ff' }} />,
      path: '/book-quote',
    },
    {
      key: 'more-tools',
      title: 'More Tools Coming',
      description: 'More useful tools will be added soon...',
      icon: <ToolOutlined style={{ fontSize: 48, color: '#52c41a' }} />,
      path: '#',
      disabled: true,
    },
  ];

  return (
    <div style={{ padding: '40px 20px', maxWidth: 1200, margin: '0 auto' }}>
      <div style={{ textAlign: 'center', marginBottom: 60 }}>
        <Title level={1}>Toolkit Hub</Title>
        <Paragraph style={{ fontSize: 16, color: '#666' }}>
          Personal toolkit collection for productivity
        </Paragraph>
      </div>

      <Row gutter={[24, 24]}>
        {tools.map((tool) => (
          <Col key={tool.key} xs={24} sm={12} md={8} lg={6}>
            <Card
              hoverable={!tool.disabled}
              style={{
                height: '100%',
                opacity: tool.disabled ? 0.6 : 1,
              }}
              onClick={() => !tool.disabled && navigate(tool.path)}
            >
              <div style={{ textAlign: 'center' }}>
                <div style={{ marginBottom: 16 }}>{tool.icon}</div>
                <Title level={4}>{tool.title}</Title>
                <Paragraph style={{ color: '#666', minHeight: 60 }}>
                  {tool.description}
                </Paragraph>
                {!tool.disabled && (
                  <Button type="primary" style={{ marginTop: 16 }}>
                    Open Tool
                  </Button>
                )}
              </div>
            </Card>
          </Col>
        ))}
      </Row>
    </div>
  );
};

export default Home;
