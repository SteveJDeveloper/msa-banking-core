package com.pichincha.dm.domain.dto.responses;

import com.pichincha.dm.domain.enums.MovementType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovementResponseDto {

  private Long id;

  private LocalDateTime date;

  private Long accountNumber;

  private MovementType movementType;

  private BigDecimal value;

  private BigDecimal balance;

}
