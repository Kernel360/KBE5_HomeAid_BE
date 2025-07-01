package com.homeaid.repository;

import com.homeaid.domain.ManagerDocument;
import com.homeaid.common.enumerate.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ManagerDocumentRepository extends JpaRepository<ManagerDocument, Long> {
    Optional<ManagerDocument> findByManagerIdAndDocumentType(Long managerId, DocumentType documentType);
}
