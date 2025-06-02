package com.homeaid.service;

import com.homeaid.domain.Reservation;
import com.homeaid.domain.Review;
import com.homeaid.domain.enumerate.ReservationStatus;
import com.homeaid.exception.CustomException;
import com.homeaid.exception.ReservationErrorCode;
import com.homeaid.exception.ReviewErrorCode;
import com.homeaid.repository.ReservationRepository;
import com.homeaid.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;

    public Review save(Review requestReview) {

        Reservation reservation = reservationRepository.findById(requestReview.getReservationId()).orElseThrow(() ->
                new CustomException(ReservationErrorCode.RESERVATION_NOT_FOUND));

        if (!reservation.getCustomerId().equals(requestReview.getWriterId())) {
            throw new CustomException(ReviewErrorCode.UNAUTHORIZED_REVIEW_ACCESS);
        }

        if (reservation.getStatus() != ReservationStatus.COMPLETED) {
            throw new CustomException(ReviewErrorCode.REVIEW_NOT_ALLOWED);
        }
        //Todo 작성자 권한이 고객이면 매니저 찜 생성 추가

        return reviewRepository.save(requestReview);
    }
}
