package com.homeaid.matching.validation;

public interface MatchingActionMemoRequest {
  Enum<?> getAction();
  String getMemo();
}
