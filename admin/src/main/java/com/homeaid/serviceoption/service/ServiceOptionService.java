package com.homeaid.serviceoption.service;

import com.homeaid.serviceoption.domain.ServiceOption;
import com.homeaid.serviceoption.domain.ServiceSubOption;
import java.util.List;

public interface ServiceOptionService {

  // 상위 옵션
  ServiceOption createOption(ServiceOption serviceOption);

  ServiceOption updateOption(Long optionId, ServiceOption serviceOption);

  void deleteOption(Long optionId);

  // 하위 옵션
  ServiceSubOption createSubOption(Long optionId, ServiceSubOption serviceSubOption);

  ServiceSubOption updateSubOption(Long subOptionId, ServiceSubOption serviceSubOption);

  void deleteSubOption(Long subOptionId);

  // 전체 옵션 조회 (상위 + 하위)
  List<ServiceOption> getAllOptions();

  ServiceOption getOptionDetail(Long optionId);
}