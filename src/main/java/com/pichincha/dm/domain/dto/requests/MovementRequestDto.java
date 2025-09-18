package com.pichincha.dm.domain.dto.requests;

import com.pichincha.dm.domain.enums.MovementType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovementRequestDto {

  @NotNull(message = "El número de cuenta es requerido")
  @Positive(message = "El número de cuenta debe ser positivo")
  private Long accountNumber;

  @NotNull(message = "El tipo de movimiento es requerido")
  private MovementType movementType;

  @NotNull(message = "El valor del movimiento es requerido")
  @Positive(message = "El valor del movimiento debe ser positivo")
  private BigDecimal value;

}
