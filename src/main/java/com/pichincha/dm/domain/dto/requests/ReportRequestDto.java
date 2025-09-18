package com.pichincha.dm.domain.dto.requests;

import java.time.LocalDate;
import lombok.Data;

@Data
public class ReportRequestDto {

  private Long clientId;

  private LocalDate dateFrom;

  private LocalDate dateLast;

}

