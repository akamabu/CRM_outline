# Kiến trúc VietCRM, rủi ro và backlog triển khai

## 1. Tổng quan kiến trúc

VietCRM được đề xuất theo hướng modular monolith trong giai đoạn đầu. Backend là một ứng dụng Spring Boot được chia theo module nghiệp vụ rõ ràng; frontend là Angular workspace có lazy-loaded features; các tác vụ tích hợp, thông báo, import/export lớn và đồng bộ được xử lý nền qua RabbitMQ hoặc worker chuyên trách.

### Module backend đề xuất

```text
backend/
├── crm-bootstrap/
├── crm-shared/
├── crm-identity/
├── crm-tenant/
├── crm-organization/
├── crm-customer/
├── crm-lead/
├── crm-sales/
├── crm-activity/
├── crm-task/
├── crm-product/
├── crm-quotation/
├── crm-contract/
├── crm-order/
├── crm-support/
├── crm-marketing/
├── crm-document/
├── crm-workflow/
├── crm-notification/
├── crm-integration/
├── crm-report/
└── crm-audit/
```

### Nguyên tắc module backend

- Controller chỉ tiếp nhận, validate request và map DTO.
- Application service điều phối use case và transaction.
- Domain chứa business rule, policy và domain event.
- Repository interface nằm trong domain; implementation nằm trong infrastructure.
- Không trả JPA Entity trực tiếp qua API.
- Module không truy cập trực tiếp bảng của module khác.
- Giao tiếp liên module thông qua application interface hoặc domain event.
- Các thao tác tích hợp phải idempotent.
- Sự kiện gửi ra broker dùng outbox pattern.

### Frontend Angular

```text
frontend/
├── src/
│   ├── app/
│   │   ├── app.config.ts
│   │   ├── app.routes.ts
│   │   ├── core/
│   │   ├── layout/
│   │   └── features/
│   ├── assets/
│   ├── environments/
│   └── styles/
├── projects/
│   ├── crm-ui/
│   ├── crm-icons/
│   └── crm-sdk/
└── e2e/
```

Frontend nên lazy-load theo feature route, dùng Signals cho state cục bộ, RxJS cho stream API/realtime/debounce và global store cho session, tenant, quyền, menu và cấu hình.

---

## 2. Rà soát kiến trúc: rủi ro và thiếu sót

### 2.1. Rủi ro về phạm vi sản phẩm

| Rủi ro | Tác động | Khuyến nghị |
| --- | --- | --- |
| Phạm vi quá rộng cho MVP | Chậm ra sản phẩm, dễ thiếu chiều sâu nghiệp vụ | Chốt vertical slice lead-to-quote trước, các module khác chỉ tạo skeleton |
| Nhiều persona nhưng chưa có workflow chi tiết | Dễ thiết kế màn hình/API lệch nhu cầu thực tế | Viết user journey cho Sales, Sales Manager, Tenant Admin và Integration App trước |
| Custom field, workflow, report builder đều phức tạp | Dễ biến thành platform lớn trước khi CRM core ổn định | Đưa bản đơn giản vào P1, không đưa bản nâng cao vào MVP |

### 2.2. Rủi ro kỹ thuật backend

| Rủi ro | Tác động | Khuyến nghị |
| --- | --- | --- |
| Modular monolith nhưng boundary không được kiểm soát | Module phụ thuộc chéo và khó tách về sau | Dùng Spring Modulith test hoặc architecture test để kiểm tra dependency |
| Multi-tenant shared schema dễ rò rỉ dữ liệu | Rủi ro bảo mật nghiêm trọng | Bắt buộc tenant filter ở repository/specification, test cross-tenant tự động |
| Outbox pattern chưa được thiết kế chi tiết | Mất event hoặc xử lý trùng event | Thiết kế bảng outbox, publisher, retry, dead-letter và idempotency key ngay từ nền tảng |
| Data scope phức tạp | Query sai quyền hoặc hiệu năng kém | Chuẩn hóa DataScopeSpecification, index theo tenant/owner/org/status |
| Workflow engine tự xây quá sớm | Tốn nguồn lực, dễ lỗi production | MVP chỉ hỗ trợ trigger/action cố định; engine tổng quát đưa sang giai đoạn sau |

### 2.3. Rủi ro frontend

| Rủi ro | Tác động | Khuyến nghị |
| --- | --- | --- |
| Tự xây UI kit quá lớn | Chậm feature delivery | Xây subset bắt buộc: shell, form, table, dialog, toast, badge, timeline |
| State management pha trộn không quy ước | Khó debug | Quy định rõ Signals/RxJS/global store/feature store và query param persistence |
| Form động cho custom field phức tạp | Dễ lỗi validation và quyền field | Bắt đầu với custom field text/number/date/select trước |

### 2.4. Rủi ro integration

| Rủi ro | Tác động | Khuyến nghị |
| --- | --- | --- |
| Connector xử lý đồng bộ khác nhau | Dữ liệu sai, khó support | Chuẩn hóa connector contract, sync cursor, mapping, conflict policy |
| Webhook bị gửi lặp | Tạo trùng record | Bắt buộc Idempotency-Key hoặc signature + event hash |
| Credential lộ qua log | Sự cố bảo mật | Masking log, credential vault, encryption at rest |

### 2.5. Thiếu sót cần bổ sung

- Chưa có quyết định rõ mô hình BFF hay SPA gọi API trực tiếp.
- Chưa có naming convention cho package, database table, event type và permission code.
- Chưa có chiến lược migration dữ liệu demo và seed tenant mặc định.
- Chưa có chính sách retention cho audit log, event log, file và sync log.
- Chưa có threat model cho OAuth/OIDC, webhook, file upload và API key.
- Chưa có tiêu chí SLO/SLA vận hành nội bộ cho production.
- Chưa có cơ chế feature flag cho module đang phát triển.
- Chưa có quy định tương thích API và event versioning.

---

## 3. Vertical slice MVP chi tiết

### 3.1. Luồng nghiệp vụ

```text
1. Website gửi lead qua webhook.
2. API Gateway chuyển request vào Integration module.
3. Integration module xác thực webhook signature và idempotency key.
4. Lead module chuẩn hóa email/phone và kiểm tra trùng.
5. Assignment service phân công sales theo round-robin đơn giản.
6. Activity module tạo activity chăm sóc đầu tiên.
7. Sales mở màn hình lead inbox và cập nhật trạng thái chăm sóc.
8. Sales convert lead thành Customer + Contact + Deal.
9. Quotation module tạo báo giá nháp từ Deal.
10. Notification module gửi in-app notification cho sales manager.
11. Audit module ghi đầy đủ thao tác.
12. Timeline hiển thị toàn bộ sự kiện của Lead/Customer/Deal.
```

### 3.2. API MVP

```http
POST   /api/v1/integrations/webhooks/leads
GET    /api/v1/leads
GET    /api/v1/leads/{id}
PATCH  /api/v1/leads/{id}
POST   /api/v1/leads/{id}/assign
POST   /api/v1/leads/{id}/convert
GET    /api/v1/customers/{id}/timeline
GET    /api/v1/deals/{id}
POST   /api/v1/deals/{id}/quotations
GET    /api/v1/notifications
PATCH  /api/v1/notifications/{id}/read
```

### 3.3. Domain events MVP

```text
lead.created
lead.duplicated_detected
lead.assigned
lead.contacted
lead.converted
customer.created
deal.created
quotation.draft_created
notification.created
audit.recorded
webhook.delivery_accepted
webhook.delivery_duplicated
```

### 3.4. Definition of Done cho vertical slice

- Có login OIDC hoặc stub auth nhất quán cho local development.
- Có tenant context và data scope tối thiểu.
- Webhook có signature/idempotency test.
- Lead inbox có loading, empty, error, forbidden state.
- Convert lead tạo đúng Customer, Contact và Deal trong cùng use case.
- Có audit log và timeline hiển thị lại được.
- Có outbox event và worker tiêu thụ notification.
- Có migration Flyway và seed data demo.
- Có OpenAPI cho API liên quan.
- Có unit, integration và E2E test cho luồng chính.

---

## 4. Backlog Epic/Story theo sprint

### Giai đoạn 0 — Nền tảng kỹ thuật

#### Sprint 1 — Repository, local platform và app shell

**Epic 0.1: Khởi tạo monorepo**

- Story: Tạo cấu trúc thư mục `frontend`, `backend`, `contracts`, `deployment`, `docs`, `samples`.
- Story: Thêm Docker Compose cho PostgreSQL, Redis, RabbitMQ, MinIO, Keycloak và mail testing server.
- Story: Thêm CI skeleton cho lint, test và build.

**Epic 0.2: Angular shell**

- Story: Tạo Angular app shell với sidebar, topbar, breadcrumb và route lazy loading.
- Story: Tạo layout responsive cơ bản.
- Story: Tạo route placeholder cho dashboard, leads, customers, deals, quotations và administration.

**Epic 0.3: Spring Boot bootstrap**

- Story: Tạo Spring Boot bootstrap app.
- Story: Tạo module shared, tenant, identity, lead, customer, sales, quotation, audit, notification, integration.
- Story: Thêm health check, structured logging và correlation ID filter.

#### Sprint 2 — Identity, tenant, permission và audit nền tảng

**Epic 0.4: Identity và tenant context**

- Story: Tích hợp Keycloak local realm hoặc auth stub cho development.
- Story: Parse JWT để lấy user, tenant và roles.
- Story: Tạo TenantContext và request filter.

**Epic 0.5: Permission framework**

- Story: Định nghĩa permission code và data scope.
- Story: Tạo annotation hoặc guard kiểm tra permission ở API.
- Story: Tạo test cross-tenant tối thiểu.

**Epic 0.6: Audit framework**

- Story: Tạo bảng audit log.
- Story: Tạo AuditService dùng chung.
- Story: Ghi audit cho login giả lập, tạo lead và convert lead.

### Giai đoạn 1 — CRM Core

#### Sprint 3 — Customer, contact và tag cơ bản

**Epic 1.1: Customer management**

- Story: CRUD customer doanh nghiệp/cá nhân.
- Story: Tìm kiếm customer theo keyword, status, owner.
- Story: Tenant filter và optimistic locking cho customer.

**Epic 1.2: Contact management**

- Story: CRUD contact.
- Story: Liên kết contact với customer.
- Story: Đánh dấu contact chính, người nhận hóa đơn, người ký hợp đồng.

**Epic 1.3: Tag và danh mục cơ bản**

- Story: CRUD tag theo tenant.
- Story: Gắn tag vào customer và lead.
- Story: Import danh mục đơn giản từ CSV.

#### Sprint 4 — Lead inbox và duplicate detection

**Epic 1.4: Lead intake**

- Story: Tạo lead thủ công.
- Story: Nhận lead qua webhook.
- Story: Chuẩn hóa phone/email.

**Epic 1.5: Duplicate detection**

- Story: Phát hiện trùng lead/customer theo phone/email/tax code.
- Story: Hiển thị cảnh báo trùng trên màn hình lead detail.
- Story: Ghi audit khi user bỏ qua cảnh báo trùng.

#### Sprint 5 — Timeline, comment, import/export

**Epic 1.6: Timeline**

- Story: Ghi timeline event cho lead/customer/deal.
- Story: Hiển thị timeline theo thứ tự thời gian.
- Story: Lọc timeline theo loại sự kiện.

**Epic 1.7: Comment và mention**

- Story: Thêm comment vào lead/customer/deal.
- Story: Mention user và tạo notification.
- Story: Sửa/xóa comment theo quyền.

**Epic 1.8: Import/export customer**

- Story: Import CSV customer nền đơn giản.
- Story: Tải file lỗi import.
- Story: Export customer theo bộ lọc và quyền export.

### Giai đoạn 2 — Lead và bán hàng

#### Sprint 6 — Assignment và lead conversion

**Epic 2.1: Lead assignment**

- Story: Rule round-robin đơn giản.
- Story: Assign lead thủ công.
- Story: Bulk assign lead.

**Epic 2.2: Lead conversion**

- Story: Convert lead thành customer/contact/deal.
- Story: Kiểm tra duplicate trước convert.
- Story: Ghi event `lead.converted` và audit đầy đủ.

#### Sprint 7 — Pipeline và deal

**Epic 2.3: Pipeline configuration**

- Story: CRUD pipeline và stage.
- Story: Cấu hình xác suất, màu, thứ tự và trạng thái thắng/thua.
- Story: Kiểm tra quyền tenant admin cho pipeline settings.

**Epic 2.4: Deal management**

- Story: CRUD deal.
- Story: Chuyển stage và ghi stage history.
- Story: Danh sách deal theo owner, stage, close date.

#### Sprint 8 — Kanban, activity, task và dashboard sales

**Epic 2.5: Kanban board**

- Story: Hiển thị deal theo pipeline stage.
- Story: Kéo thả đổi stage.
- Story: Cảnh báo field bắt buộc khi chuyển stage.

**Epic 2.6: Activity và task**

- Story: Tạo call/email/meeting/note activity.
- Story: Tạo task với assignee, deadline, priority.
- Story: Nhắc việc sắp hạn bằng notification.

**Epic 2.7: Sales dashboard cơ bản**

- Story: KPI lead mới, conversion rate, pipeline value.
- Story: Top sales theo deal won.
- Story: Deal không hoạt động.

### Giai đoạn 3 — Product và quotation

#### Sprint 9 — Product catalog và price book

- Epic 3.1: Product catalog.
- Epic 3.2: Price book.
- Epic 3.3: Deal products.

#### Sprint 10 — Quotation core

- Epic 3.4: Tạo quotation từ deal.
- Epic 3.5: Quotation line items, tax, discount và totals.
- Epic 3.6: Quotation status lifecycle.

#### Sprint 11 — PDF, email và approval

- Epic 3.7: PDF template.
- Epic 3.8: Email quotation.
- Epic 3.9: Discount approval cơ bản.

### Giai đoạn 4 — Contract và order

#### Sprint 12 — Contract core

- Epic 4.1: Contract từ deal/quotation.
- Epic 4.2: Contract approval.
- Epic 4.3: Attachment và related document.

#### Sprint 13 — Digital signature metadata và reminder

- Epic 4.4: Digital-signature status.
- Epic 4.5: Contract expiration reminder.
- Epic 4.6: Contract versioning.

#### Sprint 14 — Order và invoice summary

- Epic 4.7: Order từ quotation/contract.
- Epic 4.8: Fulfillment status.
- Epic 4.9: Invoice summary và ERP external reference.

### Giai đoạn 5 — Customer Service

#### Sprint 15–17

- Epic 5.1: Ticket CRUD và queue.
- Epic 5.2: SLA policy và tracking.
- Epic 5.3: Email-to-ticket.
- Epic 5.4: Ticket timeline và CSAT.
- Epic 5.5: Support dashboard.

### Giai đoạn 6 — Integration Hub

#### Sprint 18–20

- Epic 6.1: Connector SDK.
- Epic 6.2: Connection management và credential vault.
- Epic 6.3: Generic REST connector.
- Epic 6.4: Inbound/outbound webhook.
- Epic 6.5: Mapping editor.
- Epic 6.6: Sync engine, retry và dead-letter.
- Epic 6.7: Sync log và replay.

### Giai đoạn 7 — Workflow và report

#### Sprint 21–23

- Epic 7.1: Workflow trigger/condition/action bản đầu.
- Epic 7.2: Delay và run history.
- Epic 7.3: Notification template.
- Epic 7.4: Report builder cơ bản.
- Epic 7.5: Dashboard tùy chỉnh và scheduled report.

### Giai đoạn 8 — Production hardening

#### Sprint 24–26

- Epic 8.1: Performance tuning.
- Epic 8.2: Security hardening.
- Epic 8.3: Backup/restore và retention.
- Epic 8.4: Observability dashboard.
- Epic 8.5: Accessibility và mobile responsive.
- Epic 8.6: API docs, connector guide, sample data và upgrade procedure.

---

## 5. Task stubs triển khai ban đầu

### Repository và documentation

- [ ] Tạo monorepo structure theo đề xuất.
- [ ] Tạo `README.md` mô tả mục tiêu và quick start.
- [ ] Tạo `docs/architecture.md` mô tả kiến trúc, module boundary và roadmap.
- [ ] Tạo `docs/development/local-setup.md`.
- [ ] Tạo `docs/api/error-convention.md`.
- [ ] Tạo `docs/api/pagination-filtering.md`.

### Backend foundation

- [ ] Khởi tạo Spring Boot app.
- [ ] Tạo module `crm-shared`.
- [ ] Tạo `TenantContext`.
- [ ] Tạo `CorrelationIdFilter`.
- [ ] Tạo error response chuẩn gồm `code`, `message`, `details`, `traceId`.
- [ ] Tạo base entity gồm `id`, `tenant_id`, `version`, audit fields và soft-delete fields.
- [ ] Tạo Flyway migration đầu tiên.
- [ ] Tạo outbox table và publisher stub.
- [ ] Tạo audit log table và service stub.
- [ ] Tạo permission annotation/guard stub.

### Frontend foundation

- [ ] Khởi tạo Angular workspace.
- [ ] Tạo app shell.
- [ ] Tạo route lazy loading cho dashboard, leads, customers, deals, quotations và administration.
- [ ] Tạo HTTP interceptor cho trace ID và error mapping.
- [ ] Tạo auth/tenant/permission store.
- [ ] Tạo component nền: button, input, table, pagination, dialog, toast, badge, empty state.
- [ ] Tạo layout responsive desktop/tablet/mobile.

### Lead-to-quote MVP

- [ ] Tạo lead entity, repository, migration và API.
- [ ] Tạo webhook endpoint nhận lead.
- [ ] Tạo idempotency key table hoặc unique constraint cho webhook delivery.
- [ ] Tạo duplicate detection service theo email/phone/tax code.
- [ ] Tạo assignment service round-robin.
- [ ] Tạo lead inbox page.
- [ ] Tạo lead detail page.
- [ ] Tạo convert lead use case.
- [ ] Tạo customer/contact/deal từ lead conversion.
- [ ] Tạo quotation draft từ deal.
- [ ] Tạo notification in-app khi lead được assign hoặc quotation được tạo.
- [ ] Tạo timeline aggregate view.
- [ ] Tạo E2E test cho lead webhook đến quotation draft.

### DevOps và quality

- [ ] Tạo Docker Compose local.
- [ ] Tạo GitHub Actions lint/test/build.
- [ ] Tạo dependency scanning.
- [ ] Tạo secret scanning.
- [ ] Tạo OpenAPI generation/check.
- [ ] Tạo architecture test cho module boundary.
- [ ] Tạo test cross-tenant access denied.

---

## 6. So sánh kế hoạch với repo hiện tại

Repo hiện tại gần như trống, chỉ có `.gitkeep` và metadata Git. Chưa có cấu trúc `frontend`, `backend`, `contracts`, `deployment`, `docs` hoặc `samples` theo đề xuất. Vì vậy, kế hoạch hiện đang ở trạng thái kiến trúc/sản phẩm, chưa có implementation để đối chiếu sâu về module boundary, API, database migration hoặc UI.

### Khoảng cách chính

| Hạng mục | Trạng thái repo hiện tại | Cần bổ sung |
| --- | --- | --- |
| Documentation | Mới bổ sung README và architecture doc | Bổ sung tài liệu local setup, API convention, deployment |
| Frontend | Chưa có | Khởi tạo Angular workspace và app shell |
| Backend | Chưa có | Khởi tạo Spring Boot modular monolith |
| Database | Chưa có | Thêm PostgreSQL, Flyway và migration nền |
| Identity | Chưa có | Thêm Keycloak local realm hoặc auth stub |
| CI/CD | Chưa có | Thêm GitHub Actions |
| Contracts | Chưa có | Thêm OpenAPI/AsyncAPI skeleton |
| Deployment | Chưa có | Thêm Docker Compose local |
| Tests | Chưa có | Thêm test framework frontend/backend/E2E |

---

## 7. Ưu tiên hành động tiếp theo

1. Chốt vertical slice lead-to-quote làm MVP kỹ thuật.
2. Khởi tạo monorepo và Docker Compose local.
3. Khởi tạo Spring Boot bootstrap với tenant context, audit, permission và outbox.
4. Khởi tạo Angular shell với route lazy loading và permission-aware menu.
5. Triển khai lead webhook, lead inbox, duplicate detection, assignment và conversion.
6. Bổ sung customer, contact, deal và quotation draft tối thiểu.
7. Tạo E2E test xuyên luồng và dashboard observability tối thiểu.
