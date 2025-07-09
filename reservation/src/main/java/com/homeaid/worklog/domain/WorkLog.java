package com.homeaid.worklog.domain;

import com.homeaid.worklog.domain.enumerate.WorkType;
import com.homeaid.matching.domain.Matching;
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

    @OneToOne
    @JoinColumn(name = "matching_id", nullable = false, unique = true)
    private Matching matching;

//    @Column(name = "manager_id")
//    private Long managerId;

//    @OneToOne
//    @JoinColumn(name = "reservation_id", nullable = false, unique = true)
//    private Reservation reservation;


    @Builder
    public WorkLog(WorkType workType, Matching matching) {
        this.workType = workType;
        this.matching = matching;
    }

    public static void createWorkLog(WorkType workType, Matching matching) {
        WorkLog.builder().workType(workType).matching(matching).build();
    }

    public void updateCheckOut() {
        this.checkOutTime = LocalDateTime.now();
        this.workType = WorkType.CHECKOUT;
    }

}
