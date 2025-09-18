package com.pichincha.dm.domain.dto.responses;

import java.time.LocalDate;
import lombok.Data;

@Data
public class ReportResponseDto {

  private Long clientId;

  private LocalDate dateFrom;

  private LocalDate dateLast;

}

