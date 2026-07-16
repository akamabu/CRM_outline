package com.vietcrm.task.api;

import com.vietcrm.shared.api.ApiResponse;
import com.vietcrm.task.application.TaskApplicationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {
    private final TaskApplicationService tasks;

    public TaskController(TaskApplicationService tasks) {
        this.tasks = tasks;
    }

    @GetMapping
    public ApiResponse<List<TaskResponse>> list(@RequestParam(required = false) String tenantId, @RequestParam(required = false) String assigneeId) {
        return ApiResponse.of(tasks.list(tenantId, assigneeId).stream().map(TaskResponse::from).toList());
    }

    @PostMapping
    public ApiResponse<TaskResponse> create(@Valid @RequestBody TaskRequest request) {
        return ApiResponse.of(TaskResponse.from(tasks.create(request)));
    }

    @PostMapping("/{id}/complete")
    public ApiResponse<TaskResponse> complete(@PathVariable String id) {
        return ApiResponse.of(TaskResponse.from(tasks.complete(id)));
    }
}
