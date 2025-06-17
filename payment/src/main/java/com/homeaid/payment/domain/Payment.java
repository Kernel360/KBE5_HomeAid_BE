package com.homeaid.payment.domain;

import com.homeaid.domain.Reservation;
import com.homeaid.exception.CustomException;
import com.homeaid.payment.exception.PaymentErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "payment")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Payment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Integer amount; // 결제 금액

  @Enumerated(EnumType.STRING)
  private PaymentMethod paymentMethod;

  @Enumerated(EnumType.STRING)
  private PaymentStatus status;

  @CreatedDate
  @Column(updatable = false, nullable = false)
  private LocalDateTime paidAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "reservation_id")
  private Reservation reservation;

  @Column(nullable = false)
  private Integer refundedAmount = 0;  // 부분환불 누적 금액

  public void markRefunded() {
    this.status = PaymentStatus.REFUNDED;
    this.refundedAmount = this.amount;
  }

  public void applyPartialRefund(int refundAmount) {
    int total = this.refundedAmount + refundAmount;
    if (total > this.amount) {
      throw new CustomException(PaymentErrorCode.REFUND_AMOUNT_EXCEEDS_PAYMENT);
    }
    this.refundedAmount = total;
    if (total == this.amount) {
      this.status = PaymentStatus.REFUNDED;
    } else {
      this.status = PaymentStatus.PARTIAL_REFUNDED;
    }
  }

  public void cancelPayment() {
    this.status = PaymentStatus.CANCELED;
    this.refundedAmount = this.amount;
  }

}
