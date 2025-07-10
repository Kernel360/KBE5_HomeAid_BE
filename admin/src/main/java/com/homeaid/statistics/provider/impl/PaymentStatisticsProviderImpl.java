package com.homeaid.statistics.provider.impl;

import com.homeaid.payment.repository.PaymentRepository;
import com.homeaid.statistics.dto.PaymentStatsDto;
import com.homeaid.statistics.provider.PaymentStatisticsProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentStatisticsProviderImpl implements PaymentStatisticsProvider {

  private final PaymentRepository paymentRepository;

  @Override
  public PaymentStatsDto generate(int year, Integer month, Integer day) {
    long total = paymentRepository.sumPayments(year, month, day);
    long canceled = paymentRepository.sumCanceledPayments(year, month, day);
    long paymentCount = paymentRepository.countPayments(year, month, day);
    long cancelCount = paymentRepository.countCanceledPayments(year, month, day);

    var builder = PaymentStatsDto.builder()
        .year(year).month(month).day(day)
        .totalAmount(total)
        .cancelAmount(canceled)
        .paymentCount(paymentCount)
        .cancelCount(cancelCount);

    if (shouldIncludePaymentMethodStats(month, day)) {
      Object result = paymentRepository.findPaymentMethodSums(year, month);
      if (result instanceof Object[] values && values.length == 3) {
        builder.card(((Number) values[0]).longValue());
        builder.transfer(((Number) values[1]).longValue());
        builder.cash(((Number) values[2]).longValue());
      } else {
        builder.card(0L).transfer(0L).cash(0L);
      }
    }

    return builder.build();
  }

  private boolean shouldIncludePaymentMethodStats(Integer month, Integer day) {
    return month != null && day == null;
  }

}
