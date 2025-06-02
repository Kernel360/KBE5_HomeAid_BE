package com.homeaid.service;

import com.homeaid.domain.WorkLog;

public interface WorkLogService {
    WorkLog createWorkLog(WorkLog workLog, Long reservationId, Double longitude, Double latitude);

    WorkLog updateWorkLogForCheckOut(WorkLog workLog, Long workLogId, Double longitude, Double latitude);

}
