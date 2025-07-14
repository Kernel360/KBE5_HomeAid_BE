package com.homeaid.user.spec;

import com.homeaid.domain.Manager;
import com.homeaid.user.dto.request.AdminManagerSearchRequestDto;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public class ManagerSpecification {

  public static Specification<Manager> search(AdminManagerSearchRequestDto dto) {
    return (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (dto.getName() != null && !dto.getName().isBlank()) {
        predicates.add(cb.like(cb.lower(root.get("name")), "%" + dto.getName().toLowerCase() + "%"));
      }

      if (dto.getPhone() != null && !dto.getPhone().isBlank()) {
        predicates.add(cb.like(root.get("phone"), "%" + dto.getPhone() + "%"));
      }

      if (dto.getCareer() != null && !dto.getCareer().isBlank()) {
        predicates.add(cb.like(cb.lower(root.get("career")), "%" + dto.getCareer().toLowerCase() + "%"));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }

}
