package com.pichincha.dm.service;

import com.pichincha.dm.domain.Account;
import com.pichincha.dm.domain.dto.requests.AccountRequestDto;
import com.pichincha.dm.domain.dto.responses.AccountResponseDto;
import java.util.List;

public interface AccountService {

  List<AccountResponseDto> getAccounts();

  List<AccountResponseDto> getAccountsByClientId(Long clientId);

  AccountResponseDto getAccountById(Long id);

  AccountResponseDto saveAccount(AccountRequestDto account);

  AccountResponseDto updateAccount(Long id, AccountRequestDto account);

  void deleteAccount(Long id);

  Account findById(Long id);

  Account findByAccountNumber(Long accountNumber);

  AccountResponseDto findAccountDtoByAccountNumber(Long accountNumber);
}
