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

  private static final Map<String, String> SIDO_NORMALIZATION = Map.ofEntries(
      Map.entry("서울", "서울특별시"),
      Map.entry("부산", "부산광역시"),
      Map.entry("대구", "대구광역시"),
      Map.entry("인천", "인천광역시"),
      Map.entry("광주", "광주광역시"),
      Map.entry("대전", "대전광역시"),
      Map.entry("울산", "울산광역시"),
      Map.entry("세종", "세종특별자치시"),
      Map.entry("경기", "경기도"),
      Map.entry("강원", "강원특별자치도"),
      Map.entry("충북", "충청북도"),
      Map.entry("충남", "충청남도"),
      Map.entry("전북", "전라북도"),
      Map.entry("전남", "전라남도"),
      Map.entry("경북", "경상북도"),
      Map.entry("경남", "경상남도"),
      Map.entry("제주", "제주특별자치도")
  );

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
    return regionMap.containsKey(sido) &&
        regionMap.get(sido).contains(sigungu);
  }

  public String normalizeSido(String sido) {
    return SIDO_NORMALIZATION.getOrDefault(sido, sido);
  }
}
