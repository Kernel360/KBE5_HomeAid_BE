package com.homeaid.review.dto;

import com.homeaid.domain.enumerate.UserRole;
import lombok.Getter;

@Getter
public class ReviewSearchCondition {

  private UserRole writerRole; // CUSTOMER, MANAGER, or null (전체)

}
