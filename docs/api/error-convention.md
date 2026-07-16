# API error convention

Target error format for upcoming implementation:

```json
{
  "code": "CUSTOMER_DUPLICATED",
  "message": "Khách hàng có khả năng bị trùng",
  "details": [
    { "field": "taxCode", "code": "DUPLICATED", "message": "Mã số thuế đã tồn tại" }
  ],
  "traceId": "trace-id"
}
```
