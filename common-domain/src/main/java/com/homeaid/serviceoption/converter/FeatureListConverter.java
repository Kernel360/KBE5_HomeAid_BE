package com.homeaid.serviceoption.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Converter
public class FeatureListConverter implements AttributeConverter<List<String>, String> {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public String convertToDatabaseColumn(List<String> strings) {
    try {
      return objectMapper.writeValueAsString(strings);
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException("리스트를 JSON으로 변환할 수 없습니다.", e);
    }
  }

  @Override
  public List<String> convertToEntityAttribute(String dbData) {
    try {
      if (dbData == null || dbData.isBlank()) {
        return new ArrayList<>();
      }
      return objectMapper.readValue(dbData, new TypeReference<List<String>>() {});
    } catch (IOException e) {
      throw new IllegalArgumentException("DB의 JSON을 리스트로 변환할 수 없습니다.", e);
    }
  }
}