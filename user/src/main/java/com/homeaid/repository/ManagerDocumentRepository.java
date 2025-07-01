package com.homeaid.repository;

import com.homeaid.common.enumerate.DocumentType;
import com.homeaid.domain.ManagerDocument;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManagerDocumentRepository extends JpaRepository<ManagerDocument, Long> {

  Optional<ManagerDocument> findByManagerIdAndDocumentType(Long managerId,
      DocumentType documentType);
}
