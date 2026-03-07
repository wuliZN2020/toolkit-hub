# 🔧 代理配置问题修复指南

## 问题描述

您的系统配置了代理服务器：
- `http_proxy=http://proxy-aws-us.zhenguanyu.com:8118`
- `https_proxy=http://proxy-aws-us.zhenguanyu.com:8118`

这导致访问 localhost 时也会通过代理，造成 404 错误。

## 解决方案

### 方案1：设置 no_proxy 环境变量（推荐）

在您的 shell 配置文件中添加 `no_proxy` 设置，让 localhost 绕过代理。

#### 如果使用 zsh（默认）：

编辑 `~/.zshrc` 文件：

```bash
# 添加以下内容
export no_proxy="localhost,127.0.0.1,*.local"
export NO_PROXY="localhost,127.0.0.1,*.local"
```

然后执行：
```bash
source ~/.zshrc
```

#### 如果使用 bash：

编辑 `~/.bash_profile` 或 `~/.bashrc` 文件：

```bash
# 添加以下内容
export no_proxy="localhost,127.0.0.1,*.local"
export NO_PROXY="localhost,127.0.0.1,*.local"
```

然后执行：
```bash
source ~/.bash_profile
```

### 方案2：浏览器访问时临时方案

如果您使用浏览器访问，可以在浏览器中配置代理排除规则：

#### Chrome/Edge：
1. 设置 → 系统 → 打开代理设置
2. 在"不使用代理服务器的例外"中添加：`localhost;127.0.0.1`

#### Firefox：
1. 设置 → 网络设置
2. 在"不使用代理的地址"中添加：`localhost, 127.0.0.1`

### 方案3：修改启动脚本（临时）

我已经为您准备了修改后的启动脚本，它会自动设置 no_proxy。

## 验证修复

执行以下命令测试是否修复成功：

```bash
# 测试后端
curl http://localhost:8080/api/template/list

# 测试前端
curl http://localhost:5173
```

如果返回正常内容（而不是 Squid 代理错误），说明修复成功。

## 当前状态

✅ 服务已成功启动：
- 后端：http://localhost:8080/api
- 前端：http://localhost:5173
- API文档：http://localhost:8080/doc.html

✅ 新增的三个排版模板已成功添加：
- 文艺清新风（ID: 8）
- 数字序号风（ID: 9）
- 极简引用风（ID: 10）

## 建议

**立即执行以下命令来修复问题：**

```bash
# 1. 编辑 zsh 配置文件
echo 'export no_proxy="localhost,127.0.0.1,*.local"' >> ~/.zshrc
echo 'export NO_PROXY="localhost,127.0.0.1,*.local"' >> ~/.zshrc

# 2. 重新加载配置
source ~/.zshrc

# 3. 验证设置
echo $no_proxy

# 4. 在浏览器中打开
open http://localhost:5173
```

执行完成后，您应该能够正常访问本地服务了。
