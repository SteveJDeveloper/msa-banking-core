package com.pichincha.dm.controller;

import com.pichincha.dm.service.ReportService;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
@CrossOrigin
public class ReportController {

  private final ReportService reportService;

  @GetMapping(value = "/pdf", produces = MediaType.TEXT_PLAIN_VALUE)
  public ResponseEntity<String> generateReport(@RequestParam Long accountId,
      @RequestParam(required = false) @DateTimeFormat LocalDateTime from,
      @RequestParam(required = false) @DateTimeFormat LocalDateTime to) {
    String pdfBase64 = reportService.generateReportPdf(accountId, from, to);
    return ResponseEntity.ok(pdfBase64);
  }
}
