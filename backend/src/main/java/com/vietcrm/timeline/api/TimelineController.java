package com.vietcrm.timeline.api;

import com.vietcrm.shared.api.ApiResponse;
import com.vietcrm.timeline.application.TimelineService;
import com.vietcrm.timeline.domain.TimelineEvent;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/timeline")
public class TimelineController {
    private final TimelineService timeline;

    public TimelineController(TimelineService timeline) {
        this.timeline = timeline;
    }

    @GetMapping("/{aggregateId}")
    public ApiResponse<List<TimelineEvent>> forAggregate(@PathVariable String aggregateId) {
        return ApiResponse.of(timeline.forAggregate(aggregateId));
    }
}
