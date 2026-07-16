# VietCRM — CRM đa tổ chức

VietCRM là định hướng hệ thống CRM đa tổ chức, có thể triển khai cho một doanh nghiệp hoặc vận hành dạng SaaS. Sản phẩm tập trung vào Customer 360, lead-to-deal, báo giá, hợp đồng, đơn hàng, chăm sóc khách hàng, ticket hỗ trợ, workflow automation và integration hub.

## Mục tiêu sản phẩm

- Quản lý khách hàng 360°.
- Quản lý lead, cơ hội và quy trình bán hàng.
- Báo giá, hợp đồng, đơn hàng.
- Công việc, lịch hẹn và chăm sóc khách hàng.
- Ticket hỗ trợ và SLA.
- Workflow tự động.
- Kết nối email, tổng đài, Zalo, website, ERP, kế toán và bên thứ ba.
- Cấu hình linh hoạt theo từng doanh nghiệp.

## Nhóm người dùng chính

| Nhóm | Nhu cầu chính |
| --- | --- |
| System Admin | Quản lý hệ thống, tenant, connector |
| Tenant Admin | Cấu hình CRM của doanh nghiệp |
| Sales Manager | Quản lý pipeline, chỉ tiêu, nhân viên |
| Sales | Quản lý lead, khách hàng, cơ hội |
| Marketing | Chiến dịch, nguồn lead, phân khúc |
| Customer Service | Ticket, lịch sử chăm sóc |
| Kế toán | Xem đơn hàng, công nợ, trạng thái hóa đơn |
| Ban lãnh đạo | Dashboard và báo cáo |
| Integration App | Truy cập CRM qua API |

## Kiến trúc đề xuất

Giai đoạn đầu sử dụng **modular monolith** thay vì microservices để giảm độ phức tạp vận hành, tăng tốc phát triển sản phẩm và vẫn giữ khả năng tách module sau này.

```text
Angular CRM
    │
    ▼
API Gateway / Reverse Proxy
    │
    ▼
Spring Boot Modular Monolith
    ├── Identity & Tenant
    ├── Customer
    ├── Lead
    ├── Sales
    ├── Activity
    ├── Product
    ├── Quotation
    ├── Contract
    ├── Order
    ├── Support
    ├── Workflow
    ├── Notification
    ├── Document
    ├── Report
    └── Integration
            │
            ▼
      RabbitMQ / Event Bus
            │
     ┌──────┴─────────┐
     ▼                ▼
Integration Worker   Notification Worker
     │
     ▼
External Systems
```

## Công nghệ đề xuất

| Thành phần | Công nghệ |
| --- | --- |
| Frontend | Angular, TypeScript |
| UI foundation | Angular CDK + bộ UI nội bộ |
| State management | Signals, RxJS; NgRx khi state toàn cục phức tạp |
| Backend | Java, Spring Boot |
| Module architecture | Spring Modulith |
| Authentication | Keycloak, OpenID Connect |
| Authorization | Spring Security |
| Database | PostgreSQL |
| ORM | Spring Data JPA, Hibernate |
| Migration | Flyway |
| Cache | Redis |
| Message broker | RabbitMQ |
| File storage | MinIO hoặc S3 |
| API specification | OpenAPI |
| Event specification | AsyncAPI |
| Monitoring | Prometheus, Grafana, OpenTelemetry |
| Logging | Loki hoặc Elasticsearch |
| Container | Docker |
| CI/CD | GitHub Actions |
| Proxy | Nginx hoặc Traefik |

## MVP vertical slice đầu tiên

Mốc hợp lý đầu tiên không phải xây toàn bộ CRM, mà là hoàn thành một luồng xuyên hệ thống:

```text
Lead từ webhook
→ kiểm tra idempotency
→ phát hiện trùng
→ phân công sales
→ tạo activity chăm sóc
→ chuyển đổi thành Customer + Contact + Deal
→ tạo báo giá nháp
→ gửi thông báo
→ ghi audit, timeline và domain event
```

Mục tiêu của vertical slice là kiểm chứng đầy đủ frontend, backend, database, event bus, worker, phân quyền, audit, observability và integration contract.

## Tài liệu chi tiết

- [Kiến trúc, rủi ro, backlog và task stubs](docs/architecture.md)

## Trạng thái triển khai hiện tại

Repository đã có skeleton triển khai ban đầu cho vertical slice MVP:

- `backend/`: Spring Boot API tối thiểu cho lead intake, lead assignment và lead conversion thành Customer + Deal + Quotation nháp.
- `frontend/`: placeholder Angular route/component stubs cho lead inbox và quotation list.
- `contracts/openapi/`: OpenAPI skeleton cho các API MVP.
- `deployment/docker-compose/`: PostgreSQL, Redis, RabbitMQ và MinIO cho local development.
- `samples/`: payload webhook mẫu.

Xem hướng dẫn chạy local tại `docs/development/local-setup.md`.

## API module coverage hiện tại

Skeleton backend hiện hỗ trợ các nhóm API in-memory sau để kiểm chứng luồng nghiệp vụ trước khi gắn PostgreSQL/Flyway/Security:

| Module | API chính | Trạng thái |
| --- | --- | --- |
| Lead | `POST /api/v1/integrations/webhooks/leads`, `POST /api/v1/leads/{id}/convert` | Có create/list/assign/convert |
| Customer | `GET/POST/PATCH/DELETE /api/v1/customers` | Có CRUD tối thiểu và tìm kiếm keyword |
| Deal | `GET/POST /api/v1/deals`, `POST /api/v1/deals/{id}/stage` | Có tạo deal và đổi stage |
| Task | `GET/POST /api/v1/tasks`, `POST /api/v1/tasks/{id}/complete` | Có giao việc và hoàn thành |
| Ticket | `GET/POST /api/v1/tickets`, `POST /api/v1/tickets/{id}/close` | Có tạo ticket và đóng ticket |

## Cross-cutting MVP capabilities

- Lead webhook hỗ trợ `Idempotency-Key` để tránh tạo trùng khi hệ thống ngoài retry.
- Lead create/assign/convert hiện ghi audit log, timeline event và notification in-app in-memory.
- API lỗi validation và not-found được chuẩn hóa bằng `ApiError` và `GlobalExceptionHandler`.
