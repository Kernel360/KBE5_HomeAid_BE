package com.homeaid.domain;

import com.homeaid.domain.enumerate.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"reservationId", "writerId"}))
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long writerId;

    private Long targetId;

    private int rating;     //별점

    private String comment;

    @Enumerated(EnumType.STRING)
    private UserRole writerRole;

    @CreatedDate
    private LocalDateTime createdAt;

    private Long reservationId;

    @Builder
    public Review(Long targetId, Long writerId, UserRole writerRole, String comment, int rating, Long reservationId) {
        this.targetId = targetId;
        this.writerId = writerId;
        this.writerRole = writerRole;
        this.comment = comment;
        this.rating = rating;
        this.reservationId = reservationId;
    }
}
