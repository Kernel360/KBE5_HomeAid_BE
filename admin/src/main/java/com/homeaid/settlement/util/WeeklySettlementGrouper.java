package com.homeaid.settlement.util;

import com.homeaid.settlement.domain.Settlement;
import com.homeaid.settlement.dto.WeeklySettlementGroupDto;
import com.homeaid.settlement.dto.response.SettlementResponseDto;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class WeeklySettlementGrouper {

  public Map<String, WeeklySettlementGroupDto> groupByWeek(List<Settlement> settlements, int year, int month) {
    Map<String, WeeklySettlementGroupDto> result = new LinkedHashMap<>();
    WeekFields weekFields = WeekFields.of(Locale.KOREA); // ✅ 한국 기준 (월요일 시작)

    for (Settlement s : settlements) {
      LocalDate weekStart = s.getSettlementWeekStart();
      if (weekStart.getYear() != year || weekStart.getMonthValue() != month) continue;

      int weekOfMonth = weekStart.get(weekFields.weekOfMonth()); // ✅ 해당 날짜가 몇 주차인지

      String label = String.format("%d월 %d주차", month, weekOfMonth);
      result.putIfAbsent(label, WeeklySettlementGroupDto.builder()
          .totalAmount(0)
          .managerAmount(0)
          .adminAmount(0)
          .settlements(new ArrayList<>())
          .build());

      SettlementResponseDto dto = SettlementResponseDto.from(s);
      WeeklySettlementGroupDto group = result.get(label);

      int total = dto.getTotalAmount() != null ? dto.getTotalAmount() : 0;
      int manager = dto.getManagerAmount() != null ? dto.getManagerAmount() : 0;
      int admin = dto.getAdminAmount() != null ? dto.getAdminAmount() : 0;

      group.getSettlements().add(dto);
      group.setTotalAmount(group.getTotalAmount() + total);
      group.setManagerAmount(group.getManagerAmount() + manager);
      group.setAdminAmount(group.getAdminAmount() + admin);
    }

    return result;
  }

  private Map<String, LocalDate[]> generateWeeklyRanges(int year, int month) {
    Map<String, LocalDate[]> weeks = new LinkedHashMap<>();
    LocalDate current = LocalDate.of(year, month, 1);
    LocalDate end = current.with(TemporalAdjusters.lastDayOfMonth());
    int week = 1;

    while (!current.isAfter(end)) {
      LocalDate start = current;
      LocalDate last = start.plusDays(6);
      LocalDate actualEnd = last.isAfter(end) ? end : last;

      weeks.put(String.format("%d월 %d주차", month, week++), new LocalDate[]{start, actualEnd});
      current = actualEnd.plusDays(1);
    }

    return weeks;
  }
}