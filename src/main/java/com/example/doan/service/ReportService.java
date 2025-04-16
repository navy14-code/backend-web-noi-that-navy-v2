package com.example.doan.service;

import com.example.doan.modal.Report;
import com.example.doan.modal.User;

public interface ReportService {
    Report getReport(User user) throws Exception;
    Report updateReport(Report report) throws Exception;
}
