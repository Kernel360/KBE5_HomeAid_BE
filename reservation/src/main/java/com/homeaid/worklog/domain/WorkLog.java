package com.homeaid.worklog.domain;

import com.homeaid.worklog.domain.enumerate.WorkType;
import com.homeaid.matching.domain.Matching;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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

    private LocalDateTime checkInTime;

    private LocalDateTime checkOutTime;

    @Enumerated(EnumType.STRING)
    private WorkType workType;

    @Setter
    @OneToOne
    @JoinColumn(name = "matching_id", nullable = false, unique = true)
    private Matching matching;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime modifiedDate;


    @Builder
    public WorkLog(LocalDateTime checkInTime, LocalDateTime checkOutTime, WorkType workType, Matching matching) {
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.workType = workType;
        this.matching = matching;
    }

    public static WorkLog createWorkLog(Matching matching) {
        return WorkLog.builder().checkInTime(null).checkOutTime(null).workType(WorkType.NOT_STARTED).matching(matching).build();
    }

    public LocalDateTime updateCheckIn() {
        this.checkInTime = LocalDateTime.now();
        this.workType = WorkType.CHECKIN;
        return this.checkInTime;
    }

    public LocalDateTime updateCheckOut() {
        this.checkOutTime = LocalDateTime.now();
        this.workType = WorkType.CHECKOUT;
        return this.checkOutTime;
    }

}
