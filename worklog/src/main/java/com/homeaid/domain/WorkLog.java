package com.homeaid.domain;

import com.homeaid.domain.enumerate.WorkType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
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

    @LastModifiedDate
    private LocalDateTime checkOutTime;

    @Enumerated(EnumType.STRING)
    private WorkType workType;

    @Column(name = "manager_id")
    private Long managerId;

    @OneToOne
    @JoinColumn(name = "reservation_id", nullable = false, unique = true)
    private Reservation reservation;

    @Builder
    public WorkLog(LocalDateTime checkInTime, LocalDateTime checkOutTime, WorkType workType, Long managerId) {
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.workType = workType;
        this.managerId = managerId;
    }

    public void updateCheckOutTime(WorkLog workLog) {
        this.checkInTime = workLog.getCheckOutTime();
    }

    public void assignReservation(Reservation reservation) {
        this.reservation = reservation;
    }

}
