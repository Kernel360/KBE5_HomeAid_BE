package com.homeaid.matching.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = MatchingMemoValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface MatchingMemoConstraint {
  String message() default "거절(REJECT)일 경우 memo는 필수이며, 수락(ACCEPT)일 경우 memo는 없어야 합니다.";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}
