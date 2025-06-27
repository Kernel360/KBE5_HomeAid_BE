package com.homeaid.service;

import com.homeaid.domain.ServiceIssue;

public interface ServiceIssueService {

  void createIssue(Long reservationId, Long userId, ServiceIssue serviceIssue);

  ServiceIssue getIssueByReservation(Long reservationId, Long userId);

  ServiceIssue updateIssue(Long issueId, Long managerId, String content);

  void deleteIssue(Long issueId, Long managerId);
}
