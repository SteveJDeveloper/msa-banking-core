package com.pichincha.dm.service;

import com.pichincha.dm.domain.dto.requests.ReportRequestDto;
import java.time.LocalDate;
import java.time.LocalDateTime;

public interface ReportService {

  String generateReportPdf(Long accountId, LocalDateTime from, LocalDateTime to);
}
