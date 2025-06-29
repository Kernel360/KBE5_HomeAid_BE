package com.homeaid.domain;

import com.homeaid.domain.enumerate.WorkType;
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
@Table(name = "work_log")
public class WorkLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime checkInTime;

    private LocalDateTime checkOutTime;

    @Enumerated(EnumType.STRING)
    private WorkType workType;

    @Column(name = "manager_id")
    private Long managerId;

    @OneToOne
    @JoinColumn(name = "reservation_id", nullable = false, unique = true)
    private Reservation reservation;


    @Builder
    public WorkLog(WorkType workType, Long managerId, Reservation reservation) {
        this.workType = workType;
        this.managerId = managerId;
        this.reservation = reservation;
    }

    public void updateCheckOut() {
        this.checkOutTime = LocalDateTime.now();
        this.workType = WorkType.CHECKOUT;
    }

}
