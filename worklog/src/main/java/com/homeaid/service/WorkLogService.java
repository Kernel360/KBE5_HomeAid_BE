package com.homeaid.service;

import com.homeaid.domain.WorkLog;
import org.springframework.data.geo.Point;

public interface WorkLogService {
    WorkLog createWorkLog(WorkLog workLog, Long reservationId, Point point);

}
