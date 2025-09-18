package com.pichincha.dm.domain.dto.requests;

import com.pichincha.dm.domain.dto.validation.Create;
import com.pichincha.dm.domain.dto.validation.Update;
import com.pichincha.dm.domain.enums.AccountType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountRequestDto {

  @NotNull(message = "El número de cuenta es requerido", groups = {Create.class, Update.class})
  @Positive(message = "El número de cuenta debe ser positivo", groups = {Create.class, Update.class})
  private Long accountNumber;

  @NotNull(message = "El tipo de cuenta es requerido", groups = {Create.class, Update.class})
  private AccountType accountType;

  @NotNull(message = "El id del cliente es requerido", groups = {Create.class, Update.class})
  @Positive(message = "El id del cliente debe ser positivo", groups = {Create.class, Update.class})
  private Long clientId;

  @NotNull(message = "El balance inicial es requerido", groups = {Create.class, Update.class})
  @PositiveOrZero(message = "El balance inicial debe ser mayor o igual a cero", groups = {Create.class, Update.class})
  private BigDecimal balance;

  @NotNull(message = "El estado es requerido", groups = Update.class)
  private Boolean state;

}

