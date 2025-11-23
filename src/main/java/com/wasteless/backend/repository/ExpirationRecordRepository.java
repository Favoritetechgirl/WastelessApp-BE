package com.wasteless.backend.repository;

import com.wasteless.backend.ExpirationStatus;
import com.wasteless.backend.model.ExpirationRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpirationRecordRepository extends JpaRepository<ExpirationRecord, Long> {

    List<ExpirationRecord> findByStatus(ExpirationStatus status);
}
