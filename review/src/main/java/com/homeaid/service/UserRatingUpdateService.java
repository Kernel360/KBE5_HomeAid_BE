package com.homeaid.service;

import com.homeaid.domain.enumerate.UserRole;

public interface UserRatingUpdateService {

  void updateRating(Long targetId, UserRole writerRole);

}
