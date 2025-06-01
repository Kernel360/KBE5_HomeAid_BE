package com.homeaid.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long writerId;

    private Long targetId;

    private int rating;     //별점

    private String comment;

    @CreatedDate
    private LocalDateTime createdAt;

    private Long managerId;

    private Long reservationId;

    @Builder
    public Review(Long targetId, Long writerId, String comment, int rating, Long reservationId) {
        this.targetId = targetId;
        this.writerId = writerId;
        this.comment = comment;
        this.rating = rating;
        this.reservationId = reservationId;
    }
}
