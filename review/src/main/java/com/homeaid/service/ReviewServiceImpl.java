package com.homeaid.service;

import com.homeaid.domain.Reservation;
import com.homeaid.domain.Review;
import com.homeaid.domain.enumerate.ReservationStatus;
import com.homeaid.exception.CustomException;
import com.homeaid.exception.MatchingErrorCode;
import com.homeaid.exception.ReservationErrorCode;
import com.homeaid.exception.ReviewErrorCode;
import com.homeaid.repository.MatchingRepository;
import com.homeaid.repository.ReservationRepository;
import com.homeaid.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ReviewServiceImpl implements ReviewService {

  private final ReviewRepository reviewRepository;
  private final ReservationRepository reservationRepository;
  private final MatchingRepository matchingRepository;
  private final UserRatingUpdateService userRatingUpdateService;

  @Transactional
  @Override
  public Review createReviewByCustomer(Review requestReview) {
    Reservation validatedReservation = validateReview(requestReview);

    //예약건의 고객아이디와 요청 고객의 아이디 검증
    if (!validatedReservation.getCustomerId().equals(requestReview.getWriterId())) {
      throw new CustomException(ReviewErrorCode.UNAUTHORIZED_REVIEW_ACCESS);
    }

    //타켓 매니저 와 예약 건의 매니저 검증
    Long reservationManagerId = getFinalMatchingOfManagerId(
        validatedReservation.getFinalMatchingId());
    if (!reservationManagerId.equals(requestReview.getTargetId())) {
      throw new CustomException(ReviewErrorCode.UNAUTHORIZED_REVIEW_TARGET);
    }

    Review savedReview = reviewRepository.save(requestReview);
    userRatingUpdateService.updateRating(requestReview.getTargetId(),
        requestReview.getWriterRole());

    //Todo 매니저 찜 기능

    return savedReview;
  }

  @Transactional
  @Override
  public Review createReviewByManager(Review requestReview) {
    Reservation validatedReservation = validateReview(requestReview);

    //예약 건의 매니저와 와 요청자의 매니저 아이디 검증
    Long reservationManagerId = getFinalMatchingOfManagerId(
        validatedReservation.getFinalMatchingId());
    if (!reservationManagerId.equals(requestReview.getWriterId())) {
      throw new CustomException(ReviewErrorCode.UNAUTHORIZED_REVIEW_ACCESS);
    }

    //예약 건의 고객아이디와 요청 받은 고객아이디 검증
    if (!validatedReservation.getCustomerId().equals(requestReview.getTargetId())) {
      throw new CustomException(ReviewErrorCode.UNAUTHORIZED_REVIEW_TARGET);
    }

    Review savedReview = reviewRepository.save(requestReview);
    userRatingUpdateService.updateRating(requestReview.getTargetId(),
        requestReview.getWriterRole());

    return savedReview;
  }

  public void deleteReview(Long reviewId, Long userId) {
    Review review = reviewRepository.findById(reviewId).orElseThrow(() ->
        new CustomException(ReviewErrorCode.REVIEW_NOT_FOUND));

    if (!review.getWriterId().equals(userId)) {
      throw new CustomException(ReviewErrorCode.UNAUTHORIZED_REVIEW_ACCESS);
    }

    reviewRepository.deleteById(reviewId);
    userRatingUpdateService.updateRating(review.getTargetId(), review.getWriterRole());
  }

    @Override
    public Page<Review> getReviewOfWriter(Long writerId, Pageable pageable) {
        return reviewRepository.findByWriterId(writerId, pageable);
    }


  /**
   * 요청 받은 예약이 유요한 예약이고 완료상태 검증
   *
   * @param requestReview 요청한예약 아이디
   * @return 조회된 예약
   */
  private Reservation validateReview(Review requestReview) {
    Reservation reservation = reservationRepository.findById(requestReview.getReservationId())
        .orElseThrow(() ->
            new CustomException(ReservationErrorCode.RESERVATION_NOT_FOUND));

    if (reservation.getStatus() != ReservationStatus.COMPLETED) {
      throw new CustomException(ReviewErrorCode.REVIEW_NOT_ALLOWED);
    }
    return reservation;
  }

  private Long getFinalMatchingOfManagerId(Long finalMatchingId) {
    return matchingRepository.findById(finalMatchingId).orElseThrow(
        () -> new CustomException(MatchingErrorCode.MATCHING_NOT_FOUND)
    ).getManager().getId();
  }
}
