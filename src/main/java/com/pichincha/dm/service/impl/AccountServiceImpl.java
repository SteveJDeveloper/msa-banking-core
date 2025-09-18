package com.pichincha.dm.service.impl;

import com.pichincha.dm.domain.Account;
import com.pichincha.dm.domain.dto.requests.AccountRequestDto;
import com.pichincha.dm.domain.dto.responses.AccountResponseDto;
import com.pichincha.dm.domain.mapper.AccountMapper;
import com.pichincha.dm.exception.DuplicateResourceException;
import com.pichincha.dm.exception.ResourceNotFoundException;
import com.pichincha.dm.repository.AccountRepository;
import com.pichincha.dm.service.AccountService;
import com.pichincha.dm.service.ClientService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {

  private final AccountRepository accountRepository;

  private final AccountMapper accountMapper;

  private final ClientService clientService;

  @Override
  @Transactional(readOnly = true)
  public List<AccountResponseDto> getAccounts() {
    return accountRepository.findAll().stream()
        .map(accountMapper::toAccountResponseDto)
        .toList();
  }

  @Override
  public List<AccountResponseDto> getAccountsByClientId(Long clientId) {
    return accountRepository.findByClientId(clientId).stream()
        .map(accountMapper::toAccountResponseDto)
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public AccountResponseDto getAccountById(Long id) {
    Account account = findById(id);
    return accountMapper.toAccountResponseDto(account);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public AccountResponseDto saveAccount(AccountRequestDto accountRequestDto) {
    if (accountRepository.existsByAccountNumber(accountRequestDto.getAccountNumber())) {
      throw new DuplicateResourceException("Número de cuenta ya existe");
    }
    Account account = accountMapper.toEntity(accountRequestDto);
    account.setEnabled(true);
    account.setClient(clientService.findById(accountRequestDto.getClientId()));
    account = accountRepository.save(account);
    return accountMapper.toAccountResponseDto(account);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public AccountResponseDto updateAccount(Long id, AccountRequestDto accountRequestDto) {
    Account account = findById(id);
    if (!account.getAccountNumber().equals(accountRequestDto.getAccountNumber())
        && accountRepository.existsByAccountNumber(accountRequestDto.getAccountNumber())) {
      throw new DuplicateResourceException("Número de cuenta ya existe con otro cliente");
    }
    BeanUtils.copyProperties(accountRequestDto, account, "id");
    account.setEnabled(accountRequestDto.getState());
    account.setClient(clientService.findById(accountRequestDto.getClientId()));
    Account updated = accountRepository.save(account);
    return accountMapper.toAccountResponseDto(updated);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteAccount(Long id) {
    Account account = findById(id);
    accountRepository.delete(account);
  }

  @Override
  @Transactional(readOnly = true)
  public Account findById(Long id) {
    log.debug("Looking for account id {}", id);
    return accountRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada con id: " + id));
  }

  @Override
  public Account findByAccountNumber(Long accountNumber) {
    log.debug("Looking for account number {}", accountNumber);
    return accountRepository.findByAccountNumber(accountNumber)
        .orElseThrow(() -> new ResourceNotFoundException("N[umero de cuenta no encontrado: " + accountNumber));
  }

  @Override
  public AccountResponseDto findAccountDtoByAccountNumber(Long accountNumber) {
    Account account = accountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new ResourceNotFoundException("N[umero de cuenta no encontrado: " + accountNumber));
    return accountMapper.toAccountResponseDto(account);
  }

}
