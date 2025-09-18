package com.pichincha.dm.domain.dto.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pichincha.dm.domain.enums.AccountType;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountResponseDto {

  private Long id;

  private String name;

  private Long accountNumber;

  private AccountType accountType;

  private Long clientId;

  private BigDecimal balance;

  private Boolean state;

}

