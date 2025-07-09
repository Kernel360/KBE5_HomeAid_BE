package com.homeaid.matching.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MatchingMemoValidator implements ConstraintValidator<MatchingMemoConstraint, MatchingActionMemoRequest> {

  @Override
  public boolean isValid(MatchingActionMemoRequest dto, ConstraintValidatorContext context) {
    if (dto.getAction() == null) {
      return true;
    }

    String actionName = dto.getAction().name();

    if ("REJECT".equals(actionName)) {
      return dto.getMemo() != null && !dto.getMemo().trim().isEmpty();
    } else if ("ACCEPT".equals(actionName) || "CONFIRM".equals(actionName)) {
      return dto.getMemo() == null || dto.getMemo().trim().isEmpty();
    }

    return true;
  }
}
