package com.homeaid.service;

import com.homeaid.domain.Reservation;
import com.homeaid.domain.ServiceIssue;
import com.homeaid.exception.CustomException;
import com.homeaid.exception.ServiceIssueErrorCode;
import com.homeaid.repository.ServiceIssueRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ServiceIssueServiceImpl implements ServiceIssueService {

  private final ServiceIssueRepository serviceIssueRepository;
  private final ReservationService reservationService;

  @Override
  @Transactional
  public void createIssue(Long reservationId, Long managerId,
      ServiceIssue serviceIssue) {

    Reservation reservation = reservationService.validateReservation(reservationId);
    reservationService.validateManagerAccess(reservation, managerId);

    existsByReservationId(reservation.getId());

    ServiceIssue issue = ServiceIssue.builder()
        .reservation(reservation)
        .content(serviceIssue.getContent())
        .build();

    serviceIssueRepository.save(issue);

    // TODO 파일 추가 필요
  }

  @Override
  @Transactional(readOnly = true)
  public ServiceIssue getIssueByReservation(Long reservationId, Long userId) {

    Reservation reservation = reservationService.validateReservation(reservationId);
    reservationService.validateUserAccess(reservation, userId);

    return findByReservationId(reservationId);
  }

  @Override
  public ServiceIssue updateIssue(Long issueId, Long managerId, String content) {

    ServiceIssue issue = findIssueById(issueId);
    findIssueByIdAndManagerAccess(issueId, managerId);

    issue.updateIssue(content); // TODO 파일 수정 추가 필요

    return issue;
  }

  @Override
  public void deleteIssue(Long issueId, Long managerId) {

    ServiceIssue issue = findIssueById(issueId);
    findIssueByIdAndManagerAccess(issueId, managerId);

    serviceIssueRepository.delete(issue);
  }

  private ServiceIssue findIssueById(Long issueId) {
    return serviceIssueRepository.findByReservationId(issueId)
        .orElseThrow(() -> new CustomException(ServiceIssueErrorCode.SERVICE_ISSUE_NOT_FOUND));
  }

  private ServiceIssue findByReservationId(Long reservationId) {
    return serviceIssueRepository.findByReservationId(reservationId)
    .orElseThrow(() -> new CustomException(ServiceIssueErrorCode.SERVICE_ISSUE_NOT_FOUND));
  }

  private void findIssueByIdAndManagerAccess(Long issueId, Long managerId) {
    serviceIssueRepository.findByIdAndUserAccess(issueId, managerId)
        .orElseThrow(() -> new CustomException(ServiceIssueErrorCode.UNAUTHORIZED_SERVICE_ISSUE_ACCESS));
  }

  private void existsByReservationId(Long reservationId) {
    if (serviceIssueRepository.existsByReservationId(reservationId)) {
      throw new CustomException(ServiceIssueErrorCode.SERVICE_ISSUE_ALREADY_EXISTS);
    }
  }
}
