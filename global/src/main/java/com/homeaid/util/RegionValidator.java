package com.homeaid.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class RegionValidator {

  private final Map<String, Set<String>> regionMap = new HashMap<>();

  @PostConstruct
  public void init() {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      InputStream inputStream = getClass().getResourceAsStream("/static/korea_sido_sgg.json");

      TypeReference<Map<String, List<String>>> typeRef = new TypeReference<>() {};
      Map<String, List<String>> rawData = objectMapper.readValue(inputStream, typeRef);

      rawData.forEach((sido, sigunguList) ->
          regionMap.put(sido, new HashSet<>(sigunguList)));

    } catch (IOException e) {
      throw new IllegalStateException("지역 데이터 초기화 실패", e);
    }
  }

  public boolean isValid(String sido, String sigungu) {
    return regionMap.containsKey(sido) && regionMap.get(sido).contains(sigungu);
  }

}
