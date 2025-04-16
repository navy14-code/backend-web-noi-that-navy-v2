package com.example.doan.service.impl;

import com.example.doan.modal.Report;
import com.example.doan.modal.User;
import com.example.doan.repository.ReportRepository;
import com.example.doan.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private final ReportRepository reportRepository;
    @Override
    public Report getReport(User user) throws Exception {

        Report report = reportRepository.findByUserId(user.getId());

        if (report == null) {
            Report newReport = new Report();
            newReport.setId(user.getId());
            return reportRepository.save(newReport);
        }
        return report;
    }

    @Override
    public Report updateReport(Report report) throws Exception {
        return reportRepository.save(report);
    }
}
