package com.b2b.b2b.modules.activitylog.repository;

import com.b2b.b2b.modules.activitylog.entity.ActivityLogs;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityLogsRepository extends JpaRepository<ActivityLogs, Integer> {
}
