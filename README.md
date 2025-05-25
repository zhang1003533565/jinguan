# 地铁隧道监测系统

基于Vue3 + Spring Boot + Uni-app的地铁隧道监测系统，包含Web管理端和移动端小程序。

## 项目结构

```bash
suidao/
├── backed/               # 后端项目
│   ├── src/             # 源代码
│   ├── target/          # 编译输出
│   ├── pom.xml         # Maven配置
│   └── README.md       # 后端说明文档
├── vue/                 # 前端项目
│   ├── src/            # 源代码
│   ├── public/         # 静态资源
│   ├── package.json    # 依赖配置
│   ├── vite.config.js  # Vite配置
│   └── nginx.conf      # Nginx配置
└── README.md           # 项目说明文档
```

## 技术栈

### 前端（Web管理端）
- Vue 3
- Vite
- Tailwind CSS
- ECharts
- Font Awesome

### 后端
- Spring Boot
- MyBatis-Plus
- MySQL
- Redis
- WebSocket

### 移动端
- uni-app

## 功能特性

- 实时监测数据展示
- 沉降数据分析
- 环境监测（温度、湿度、瓦斯等）
- 事件日志记录
- 预警信息推送
- 设备管理
- 权限控制

## 快速开始

### 后端启动
```bash
cd backed
mvn clean install
mvn spring-boot:run
```

### 前端启动
```bash
cd vue
npm install
npm run dev
```

### 生产环境部署
```bash
# 前端打包
cd vue
npm run build

# 后端打包
cd backed
mvn clean package
```

## 开发环境要求

- Node.js >= 16
- JDK >= 1.8
- Maven >= 3.6
- MySQL >= 5.7
- Redis >= 6.0

## Docker部署

前端Dockerfile已配置，可以直接构建：

```bash
cd vue
docker build -t suidao-frontend .
docker run -d -p 80:80 suidao-frontend
```

## 配置说明

### 前端配置
- 环境变量配置在`.env`文件中
- Vite配置在`vite.config.js`中
- Tailwind配置在`tailwind.config.js`中
- Nginx配置在`nginx.conf`中

### 后端配置
- 应用配置在`application.yml`中
- 数据库配置在`application-dev.yml`中
- 日志配置在`logback-spring.xml`中

## 开发规范

1. Git提交规范
   - feat: 新功能
   - fix: 修复bug
   - docs: 文档更新
   - style: 代码格式化
   - refactor: 重构
   - test: 测试用例
   - chore: 其他修改

2. 代码规范
   - 使用ESLint进行代码检查
   - 使用Prettier进行代码格式化
   - 遵循阿里巴巴Java开发规范

## 贡献指南

1. Fork本仓库
2. 创建特性分支
3. 提交代码
4. 创建Pull Request

## 版本历史

- v1.0.0 (2024-01) - 初始版本发布
  - 基础监测功能
  - 数据可视化
  - 实时预警

## 许可证

[MIT License](LICENSE) 