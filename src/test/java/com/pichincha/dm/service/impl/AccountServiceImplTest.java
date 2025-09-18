package com.pichincha.dm.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.pichincha.dm.domain.Account;
import com.pichincha.dm.domain.Client;
import com.pichincha.dm.domain.dto.requests.AccountRequestDto;
import com.pichincha.dm.domain.dto.responses.AccountResponseDto;
import com.pichincha.dm.domain.mapper.AccountMapper;
import com.pichincha.dm.exception.DuplicateResourceException;
import com.pichincha.dm.exception.ResourceNotFoundException;
import com.pichincha.dm.repository.AccountRepository;
import com.pichincha.dm.service.ClientService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

  @InjectMocks
  private AccountServiceImpl accountService;

  @Mock
  private AccountRepository accountRepository;

  @Mock
  private AccountMapper accountMapper;

  @Mock
  private ClientService clientService;

  private Account account;

  private AccountRequestDto accountRequestDto;

  private AccountResponseDto accountResponseDto;

  private Client client;

  @BeforeEach
  void setUp() {
    client = new Client();
    client.setId(1L);

    account = new Account();
    account.setId(1L);
    account.setAccountNumber(1234567890L);
    account.setEnabled(true);
    account.setClient(client);

    accountRequestDto = new AccountRequestDto();
    accountRequestDto.setAccountNumber(1234567890L);
    accountRequestDto.setClientId(1L);
    accountRequestDto.setState(true);

    accountResponseDto = new AccountResponseDto();
    accountResponseDto.setAccountNumber(account.getAccountNumber());
    accountResponseDto.setAccountType(account.getAccountType());
    accountResponseDto.setName(account.getClient().getName());
    accountResponseDto.setState(account.isEnabled());
  }

  @Test
  void getAccounts_ShouldReturnListOfAccountDtos() {
    when(accountRepository.findAll()).thenReturn(List.of(account));
    when(accountMapper.toAccountResponseDto(account)).thenReturn(accountResponseDto);

    List<AccountResponseDto> result = accountService.getAccounts();

    assertEquals(1, result.size());
    assertEquals(accountResponseDto, result.get(0));
    verify(accountRepository).findAll();
  }

  @Test
  void getAccountById_ShouldReturnAccountDto() {
    when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
    when(accountMapper.toAccountResponseDto(account)).thenReturn(accountResponseDto);

    AccountResponseDto result = accountService.getAccountById(1L);

    assertEquals(accountResponseDto, result);
  }

  @Test
  void getAccountById_ShouldThrow_WhenNotFound() {
    when(accountRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> accountService.getAccountById(1L));
  }

  @Test
  void saveAccount_ShouldSaveAndReturnDto_WhenAccountNumberIsUnique() {
    when(accountRepository.existsByAccountNumber(accountRequestDto.getAccountNumber())).thenReturn(false);
    when(accountMapper.toEntity(accountRequestDto)).thenReturn(account);
    when(clientService.findById(1L)).thenReturn(client);
    when(accountRepository.save(account)).thenReturn(account);
    when(accountMapper.toAccountResponseDto(account)).thenReturn(accountResponseDto);

    AccountResponseDto result = accountService.saveAccount(accountRequestDto);

    assertNotNull(result);
    assertEquals(accountResponseDto, result);
    verify(accountRepository).save(account);
  }

  @Test
  void saveAccount_ShouldThrow_WhenAccountNumberExists() {
    when(accountRepository.existsByAccountNumber(accountRequestDto.getAccountNumber())).thenReturn(true);

    assertThrows(DuplicateResourceException.class, () -> accountService.saveAccount(
        accountRequestDto));
  }

  @Test
  void updateAccount_ShouldUpdateAndReturnDto_WhenAccountNumberUnchanged() {
    when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
    when(accountMapper.toEntity(accountRequestDto)).thenReturn(account);
    when(clientService.findById(1L)).thenReturn(client);
    when(accountRepository.save(account)).thenReturn(account);
    when(accountMapper.toAccountResponseDto(account)).thenReturn(accountResponseDto);

    AccountResponseDto result = accountService.updateAccount(1L, accountRequestDto);

    assertEquals(accountResponseDto, result);
    verify(accountRepository).save(account);
  }

  @Test
  void updateAccount_ShouldThrow_WhenNewAccountNumberExists() {
    Account existing = new Account();
    existing.setId(1L);
    existing.setAccountNumber(9999999999L);

    accountRequestDto.setAccountNumber(1234567890L); // New number
    when(accountRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(accountRepository.existsByAccountNumber(1234567890L)).thenReturn(true);

    assertThrows(DuplicateResourceException.class, () -> accountService.updateAccount(1L,
        accountRequestDto));
  }

  @Test
  void deleteAccount_ShouldDelete_WhenAccountExists() {
    when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

    accountService.deleteAccount(1L);

    verify(accountRepository).delete(account);
  }

  @Test
  void deleteAccount_ShouldThrow_WhenNotFound() {
    when(accountRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> accountService.deleteAccount(1L));
  }

  @Test
  void findById_ShouldReturnAccount_WhenExists() {
    when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

    Account result = accountService.findById(1L);

    assertEquals(account, result);
  }

  @Test
  void findById_ShouldThrow_WhenNotFound() {
    when(accountRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> accountService.findById(1L));
  }

  @Test
  void findByAccountNumber_ShouldReturnAccount_WhenExists() {
    when(accountRepository.findByAccountNumber(1234567890L)).thenReturn(Optional.of(account));

    Account result = accountService.findByAccountNumber(1234567890L);

    assertEquals(account, result);
  }

  @Test
  void findByAccountNumber_ShouldThrow_WhenNotFound() {
    when(accountRepository.findByAccountNumber(1234567890L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> accountService.findByAccountNumber(1234567890L));
  }
}
