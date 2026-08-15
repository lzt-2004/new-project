# FulfillFlow Backend

> FulfillFlow 的 Java 后端服务，负责库存、订单和履约核心业务。

## 当前范围（Issue #5）

- Java 17、Spring Boot 3、Maven
- MySQL 数据库配置与本机配置文件隔离
- Flyway（数据库版本迁移）管理表结构；JPA 固定使用 `ddl-auto=validate`
- Actuator 健康检查接口与 Swagger API 文档
- MockMvc（Spring MVC 接口测试）测试基线
- GitHub Actions CI：自动运行测试、打包后端，并构建前端
- SKU 与库存表：可售库存、预占库存、版本号与更新时间
- 创建 SKU、查询库存、补货接口
- 创建订单时条件预占库存，并保存订单单价/总金额快照
- 订单创建失败时，库存预占与订单写入同库事务回滚
- 幂等取消订单：只释放一次预占库存
- 模拟支付确认：订单状态流转与支付后库存不变

## 库存规则

- `available_stock`（可售库存）：可被新订单预占的库存。
- `reserved_stock`（预占库存）：已分配给待处理订单、暂时不能继续出售的库存。
- 补货数量必须大于零，只增加 `available_stock`，同时更新 `version`（版本号）。
- 创建订单时，通过带条件的更新将可售库存转为预占库存；只有 `available_stock >= quantity` 时才会更新成功。
- 创建订单和库存预占处于同一个 MySQL 事务中：任一步失败，全部回滚。
- 支付 `PENDING`（待支付）订单时，状态变为 `PAID`（已支付）；订单创建时已预占库存，因此支付不修改库存。重复支付直接返回已支付订单。
- 取消 `PENDING`（待支付）订单时，将同等数量从预占库存释放回可售库存；重复取消不会重复释放。
- `PAID` 订单不能取消，`CANCELLED` 订单不能支付，均返回 HTTP `409`。

## 本地环境

- Java 17
- Maven 3.9+（或 Maven 容器）
- MySQL 8，且已创建 `fulfillflow` 数据库和项目账号
- Redis 当前仅保留配置，Issue #4 尚未执行 Redis 操作

## 数据库配置

真实账号密码放在本机的 `src/main/resources/application.yml`，该文件已被仓库根目录的 `.gitignore` 忽略，不应提交。

可提交的配置模板见 `src/main/resources/application-demo.yml`。复制后按本机 MySQL 修改即可，不能在模板中填写真实密码或 Token（访问令牌）。

## 测试验证

```bash
mvn clean test
```

测试使用 H2 内存数据库，并运行 Flyway V1、V2、V3 迁移。当前覆盖：

- `GET /api/system/ping` 统一成功响应
- 创建 SKU、正数补货及库存字段变化
- 补货数量为零时返回 HTTP `400`
- 创建订单时预占库存与订单价格快照
- 库存不足返回 HTTP `409`，且库存不变
- 订单落库失败时回滚库存预占
- 取消订单释放预占库存
- 重复取消不重复释放库存
- 取消不存在订单返回 HTTP `404`
- 支付成功、重复支付、已取消订单支付冲突、已支付订单取消冲突

## 启动后端

```bash
mvn spring-boot:run
```

MySQL 配置正确且应用启动成功后，可访问：

- 健康检查：`http://localhost:8081/actuator/health`
- 示例接口：`http://localhost:8081/api/system/ping`
- Swagger：`http://localhost:8081/swagger-ui.html`

## 数据库迁移规则

不要修改已经在任何环境执行过的 Flyway 迁移文件。表结构或历史数据需要修正时，新增一个迁移文件，例如：

```text
V4__add_order_expire_at.sql
```

## 后续计划

1. Docker Compose 与本地部署文档。
2. 对接 `../fulfillflow-web` 前端管理台，完成一次真实接口联调。
3. 订单查询、超时取消与预占库存释放。
