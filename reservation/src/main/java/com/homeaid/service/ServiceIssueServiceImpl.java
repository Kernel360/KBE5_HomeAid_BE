package com.homeaid.service;

import com.homeaid.domain.Reservation;
import com.homeaid.domain.ServiceIssue;
import com.homeaid.exception.CustomException;
import com.homeaid.exception.ServiceIssueErrorCode;
import com.homeaid.repository.ServiceIssueRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ServiceIssueServiceImpl implements ServiceIssueService {

  private final ServiceIssueRepository serviceIssueRepository;
  private final ReservationService reservationService;
  private final ServiceIssueFileService serviceIssueFileService;

  @Override
  @Transactional
  public void createIssue(Long reservationId, Long managerId, String content, List<MultipartFile> files) {

    Reservation reservation = reservationService.validateReservation(reservationId);
    reservationService.validateManagerAccess(reservation, managerId);
    existsByReservationId(reservation.getId());

    ServiceIssue issue = ServiceIssue.builder()
        .reservation(reservation)
        .content(content)
        .build();

    serviceIssueFileService.uploadFiles(issue, files);
    serviceIssueRepository.save(issue);
  }

  @Override
  @Transactional(readOnly = true)
  public ServiceIssue getIssueByReservation(Long reservationId, Long userId) {

    Reservation reservation = reservationService.validateReservation(reservationId);
    reservationService.validateUserAccess(reservation, userId);

    return findByReservationId(reservationId);
  }

  @Override
  @Transactional
  public ServiceIssue updateIssue(Long issueId, Long managerId, String content, List<MultipartFile> files) {

    ServiceIssue issue = findIssueById(issueId);
    findIssueByIdAndManagerAccess(issue.getId(), managerId);

    issue.updateIssue(content);
    serviceIssueFileService.updateFiles(issue, files);

    return issue;
  }

  @Override
  @Transactional
  public void deleteIssue(Long issueId, Long managerId) {

    ServiceIssue issue = findIssueById(issueId);
    findIssueByIdAndManagerAccess(issueId, managerId);

    serviceIssueRepository.delete(issue);
    serviceIssueFileService.deleteFiles(issue);
  }

  private ServiceIssue findIssueById(Long issueId) {
    return serviceIssueRepository.findById(issueId)
        .orElseThrow(() -> new CustomException(ServiceIssueErrorCode.SERVICE_ISSUE_NOT_FOUND));
  }

  private ServiceIssue findByReservationId(Long reservationId) {
    return serviceIssueRepository.findByReservationId(reservationId)
    .orElseThrow(() -> new CustomException(ServiceIssueErrorCode.SERVICE_ISSUE_NOT_FOUND));
  }

  private void findIssueByIdAndManagerAccess(Long issueId, Long managerId) {
    serviceIssueRepository.findByIdAndManagerAccess(issueId, managerId)
        .orElseThrow(() -> new CustomException(ServiceIssueErrorCode.UNAUTHORIZED_SERVICE_ISSUE_ACCESS));
  }

  private void existsByReservationId(Long reservationId) {
    if (serviceIssueRepository.existsByReservationId(reservationId)) {
      throw new CustomException(ServiceIssueErrorCode.SERVICE_ISSUE_ALREADY_EXISTS);
    }
  }
}
