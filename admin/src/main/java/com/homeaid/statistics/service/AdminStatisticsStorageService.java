package com.homeaid.statistics.service;

import com.homeaid.statistics.dto.AdminStatisticsDto;

public interface AdminStatisticsStorageService {

  void save(AdminStatisticsDto dto);

  AdminStatisticsDto loadOrThrow(int year, Integer month, Integer day);

}
