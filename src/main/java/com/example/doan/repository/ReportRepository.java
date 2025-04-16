package com.example.doan.repository;

import com.example.doan.modal.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
    Report findByUserId(long userId);
}
