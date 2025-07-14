package com.homeaid.user.spec;

import com.homeaid.domain.Customer;
import com.homeaid.user.dto.request.AdminCustomerSearchRequestDto;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public class CustomerSpecification {

  public static Specification<Customer> search(AdminCustomerSearchRequestDto dto) {
    return (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (dto.getName() != null && !dto.getName().isBlank()) {
        predicates.add(cb.like(cb.lower(root.get("name")), "%" + dto.getName().toLowerCase() + "%"));
      }

      if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
        predicates.add(cb.like(cb.lower(root.get("email")), "%" + dto.getEmail().toLowerCase() + "%"));
      }

      if (dto.getPhone() != null && !dto.getPhone().isBlank()) {
        predicates.add(cb.like(cb.lower(root.get("phone")), "%" + dto.getPhone().toLowerCase() + "%"));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }

}
