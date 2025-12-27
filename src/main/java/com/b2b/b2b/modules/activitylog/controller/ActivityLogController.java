package com.b2b.b2b.modules.activitylog.controller;

import com.b2b.b2b.modules.activitylog.entity.EntityType;
import com.b2b.b2b.modules.activitylog.payloads.ActivityLogsResponseDTO;
import com.b2b.b2b.modules.activitylog.service.ActivityLogsService;
import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.shared.AuthUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ActivityLogController {
    private final AuthUtil authUtil;
    private final ActivityLogsService activityLogsService;

    public ActivityLogController(AuthUtil authUtil, ActivityLogsService activityLogsService) {
        this.authUtil = authUtil;
        this.activityLogsService = activityLogsService;
    }
//
//    @GetMapping("app/v1/activity-logs")
//    public ResponseEntity<Page<ActivityLogsResponseDTO>> getActivityLogs(
//    @RequestParam(required = false)EntityType type,
//    @RequestParam(required = false) Integer days,
//    @PageableDefault(
//            size= 20,
//            sort = "createdAt",
//            direction = Sort.Direction.DESC
//    )Pageable pageable
//    ) {
//    User user = authUtil.loggedInUser();
//    Page<ActivityLogsResponseDTO> logs = activityLogsService.getFilteredLogs(type,days,pageable);
//    return ResponseEntity.ok(logs);
//
//   }
}
