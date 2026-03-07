import { useNavigate } from 'react-router-dom';
import { BookOutlined, ToolOutlined, RightOutlined } from '@ant-design/icons';
import './Home.css';

const Home = () => {
  const navigate = useNavigate();

  const tools = [
    {
      key: 'book-quote',
      title: '书摘收集',
      subtitle: '记录阅读的温度',
      description: '搜索书籍，收集打动你的句子，一键整理分享',
      icon: <BookOutlined />,
      path: '/book-quote',
      disabled: false,
    },
    {
      key: 'more-tools',
      title: '更多工具',
      subtitle: '持续更新中',
      description: '更多实用工具正在开发，敬请期待',
      icon: <ToolOutlined />,
      path: '#',
      disabled: true,
    },
  ];

  return (
    <div className="home-container">
      <div className="home-content">
        {/* 页头 */}
        <header className="home-header">
          <div className="home-badge">工具集</div>
          <h1 className="home-title">
            简单<span className="highlight">工具</span>
          </h1>
          <p className="home-subtitle">
            让日常工作更轻松
          </p>
        </header>

        {/* 工具卡片 */}
        <div className="tools-grid">
          {tools.map((tool) => (
            <div
              key={tool.key}
              className={`tool-card ${tool.disabled ? 'disabled' : ''}`}
              onClick={() => !tool.disabled && navigate(tool.path)}
            >
              <div className="tool-card-inner">
                <div className="tool-icon">
                  {tool.icon}
                </div>
                <div className="tool-content">
                  <h2 className="tool-title">{tool.title}</h2>
                  <p className="tool-subtitle">{tool.subtitle}</p>
                  <p className="tool-description">{tool.description}</p>
                </div>
                {!tool.disabled && (
                  <div className="tool-action">
                    <span>立即使用</span>
                    <RightOutlined />
                  </div>
                )}
                {tool.disabled && (
                  <div className="tool-badge-coming">即将推出</div>
                )}
              </div>
              <div className="tool-card-shine"></div>
            </div>
          ))}
        </div>

        {/* 页脚信息 */}
        <footer className="home-footer">
          <div className="footer-divider"></div>
          <p className="footer-text">
            © 2026 工具箱 · 简单实用
          </p>
        </footer>
      </div>
    </div>
  );
};

export default Home;
