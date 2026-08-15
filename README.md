# FulfillFlow

> 库存、订单与履约场景的全栈练习项目。仓库采用 monorepo（单仓库多项目）结构，前后端保持独立构建与运行，并在部署阶段通过 API 和 Nginx（反向代理）协作。

## 项目结构

```text
FulfillFlow/
├─ fulfillflow-backend/     Java Spring Boot 后端
│  ├─ src/                  业务源码、测试和 Flyway 迁移
│  ├─ docs/                 接口、Issue 与部署文档
│  ├─ deploy/               部署配置
│  └─ pom.xml               Maven 构建配置
├─ fulfillflow-web/         Vue 3 前端管理台
│  ├─ src/                  页面、接口封装和样式
│  ├─ package.json          npm 依赖与脚本
│  └─ vite.config.ts        Vite 开发代理配置
├─ .github/                 CI、Issue 和 PR 模板
└─ README.md                项目总览
```

## 当前能力

- SKU 创建、库存查询与补货
- 条件更新预占库存，避免并发下的负库存
- 创建订单与价格快照
- 订单创建失败时回滚库存预占
- 幂等取消订单并释放预占库存
- 模拟支付确认与 `PENDING -> PAID` 状态流转，支付后库存不变
- Flyway 数据库版本迁移
- MockMvc 接口测试与 GitHub Actions CI
- Vue 管理台：库存操作、订单操作与请求日志

## 本地启动

后端在 WSL 或具备 Java/Maven/MySQL 的终端启动：

```bash
cd fulfillflow-backend
mvn clean test
mvn spring-boot:run
```

后端成功启动后监听 `http://localhost:8081`。

前端在 PowerShell 或 WSL 启动：

```bash
cd fulfillflow-web
npm ci
npm run dev
```

前端开发地址为 `http://localhost:5173`。Vite 会将 `/api` 与 `/actuator` 代理到后端 `8081` 端口。

## 配置安全

后端真实数据库配置位于 `fulfillflow-backend/src/main/resources/application.yml`，该文件只保留在本机并已被 Git 忽略。可提交的无密码模板是 `fulfillflow-backend/src/main/resources/application-demo.yml`。

不要提交数据库密码、Token（访问令牌）、`.env` 文件、`node_modules` 或任何构建产物。

## 构建验证

```bash
cd fulfillflow-backend
mvn clean test
```

```bash
cd fulfillflow-web
npm ci
npm run build
```

后端详细说明见 [后端 README](fulfillflow-backend/README.md)，前端详细说明见 [前端 README](fulfillflow-web/README.md)。
