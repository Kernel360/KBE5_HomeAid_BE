package com.homeaid.serviceoption.service;

import com.homeaid.exception.CustomException;
import com.homeaid.serviceoption.domain.ServiceOption;
import com.homeaid.serviceoption.domain.ServiceSubOption;
import com.homeaid.serviceoption.exception.ServiceOptionErrorCode;
import com.homeaid.serviceoption.repository.ServiceOptionRepository;
import com.homeaid.serviceoption.repository.ServiceSubOptionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ServiceOptionServiceImpl implements ServiceOptionService {

  private final ServiceOptionRepository serviceOptionRepository;
  private final ServiceSubOptionRepository serviceSubOptionRepository;

  @Override
  @Transactional
  public ServiceOption createOption(ServiceOption serviceOption) {

    return serviceOptionRepository.save(serviceOption);
  }

  @Override
  @Transactional
  public ServiceOption updateOption(Long optionId, ServiceOption serviceOption) {
    ServiceOption option = serviceOptionRepository.findById(optionId)
        .orElseThrow(() -> new CustomException(ServiceOptionErrorCode.OPTION_NOT_FOUND));

    option.update(serviceOption.getName(), serviceOption.getDescription());
    return serviceOptionRepository.save(option);
  }

  @Override
  @Transactional
  public void deleteOption(Long optionId) {
    ServiceOption option = serviceOptionRepository.findById(optionId)
        .orElseThrow(() -> new CustomException(ServiceOptionErrorCode.OPTION_NOT_FOUND));
    serviceOptionRepository.delete(option);
  }

  @Override
  @Transactional
  public ServiceSubOption createSubOption(Long optionId,
      ServiceSubOption serviceSubOption) {
    ServiceOption option = serviceOptionRepository.findById(optionId)
        .orElseThrow(() -> new CustomException(ServiceOptionErrorCode.OPTION_NOT_FOUND));

    option.addSubOption(serviceSubOption);
    return serviceSubOptionRepository.save(serviceSubOption);
  }

  @Override
  @Transactional
  public ServiceSubOption updateSubOption(Long subOptionId,
      ServiceSubOption serviceSubOption) {
    ServiceSubOption subOption = serviceSubOptionRepository.findById(subOptionId)
        .orElseThrow(() -> new CustomException(ServiceOptionErrorCode.SUB_OPTION_NOT_FOUND));

    subOption.update(
        serviceSubOption.getName(),
        serviceSubOption.getDescription(),
        serviceSubOption.getDurationMinutes(),
        serviceSubOption.getBasePrice()
    );

    return subOption;
  }

  @Override
  @Transactional
  public void deleteSubOption(Long subOptionId) {
    ServiceSubOption subOption = serviceSubOptionRepository.findById(subOptionId)
        .orElseThrow(() -> new CustomException(ServiceOptionErrorCode.SUB_OPTION_NOT_FOUND));
    serviceSubOptionRepository.delete(subOption);
  }

  @Override
  @Transactional(readOnly = true)
  public List<ServiceOption> getAllOptions() {
    return serviceOptionRepository.findAll();
  }

  @Override
  public ServiceOption getOptionDetail(Long optionId) {
    return serviceOptionRepository.findById(optionId)
        .orElseThrow(() -> new CustomException(ServiceOptionErrorCode.OPTION_NOT_FOUND));
  }
}
