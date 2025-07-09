package com.homeaid.issue.service;

import com.homeaid.reservation.domain.Reservation;
import com.homeaid.issue.domain.ServiceIssue;
import com.homeaid.exception.CustomException;
import com.homeaid.issue.exception.ServiceIssueErrorCode;
import com.homeaid.issue.repository.ServiceIssueRepository;
import com.homeaid.reservation.service.ReservationService;
import java.io.FileNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
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
  public void createIssue(Long reservationId, Long managerId, String content,
      List<MultipartFile> files) {

    Reservation reservation = reservationService.validateReservation(reservationId);
    reservationService.validateManagerAccess(reservation, managerId);
    existsByReservationId(reservation.getId());

    ServiceIssue issue = ServiceIssue.builder()
        .reservation(reservation)
        .content(content)
        .build();

    serviceIssueFileService.uploadFiles(issue, files);

    try {
      serviceIssueRepository.save(issue);
    } catch (DataIntegrityViolationException e) {
      // unique 제약조건 위반 처리
      throw new CustomException(ServiceIssueErrorCode.SERVICE_ISSUE_ALREADY_EXISTS);
    }
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
  public ServiceIssue updateIssue(Long issueId, Long managerId, String content,
      List<MultipartFile> files, List<Long> deleteImageIds) {

    ServiceIssue issue = findIssueByIdAndManagerAccess(issueId, managerId);

    issue.updateIssue(content);
    serviceIssueFileService.updateFiles(issue, files, deleteImageIds);

    return issue;
  }

  @Override
  @Transactional
  public void deleteIssue(Long issueId, Long managerId) throws FileNotFoundException {

    ServiceIssue issue = findIssueById(issueId);
    findIssueByIdAndManagerAccess(issueId, managerId);

    serviceIssueFileService.deleteFiles(issue);
    serviceIssueRepository.delete(issue);
  }

  private ServiceIssue findIssueById(Long issueId) {
    return serviceIssueRepository.findByIdWithImages(issueId)
        .orElseThrow(() -> new CustomException(ServiceIssueErrorCode.SERVICE_ISSUE_NOT_FOUND));
  }

  private ServiceIssue findByReservationId(Long reservationId) {
    return serviceIssueRepository.findByReservationId(reservationId)
        .orElseThrow(() -> new CustomException(ServiceIssueErrorCode.SERVICE_ISSUE_NOT_FOUND));
  }

  private ServiceIssue findIssueByIdAndManagerAccess(Long issueId, Long managerId) {
    return serviceIssueRepository.findByIdAndManagerAccess(issueId, managerId)
        .orElseThrow(
            () -> new CustomException(ServiceIssueErrorCode.UNAUTHORIZED_SERVICE_ISSUE_ACCESS));
  }

  private void existsByReservationId(Long reservationId) {
    if (serviceIssueRepository.existsByReservationId(reservationId)) {
      throw new CustomException(ServiceIssueErrorCode.SERVICE_ISSUE_ALREADY_EXISTS);
    }
  }
}
