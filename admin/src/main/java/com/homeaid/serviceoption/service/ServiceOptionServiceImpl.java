package com.homeaid.serviceoption.service;

import com.homeaid.exception.CustomException;
import com.homeaid.serviceoption.domain.ServiceOption;
import com.homeaid.serviceoption.exception.ServiceOptionErrorCode;
import com.homeaid.serviceoption.repository.ServiceOptionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ServiceOptionServiceImpl implements ServiceOptionService {

  private final ServiceOptionRepository serviceOptionRepository;

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

    option.update(serviceOption.getName(), 20000);
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
