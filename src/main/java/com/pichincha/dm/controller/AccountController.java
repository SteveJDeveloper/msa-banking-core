package com.pichincha.dm.controller;

import com.pichincha.dm.domain.dto.requests.AccountRequestDto;
import com.pichincha.dm.domain.dto.responses.AccountResponseDto;
import com.pichincha.dm.domain.dto.validation.Create;
import com.pichincha.dm.domain.dto.validation.Update;
import com.pichincha.dm.service.AccountService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class AccountController {

  private final AccountService accountService;

  @GetMapping
  public ResponseEntity<List<AccountResponseDto>> getAccounts() {
    log.debug("Starting getAccounts endpoint");
    List<AccountResponseDto> result = accountService.getAccounts();
    log.debug("Finishing getAccounts endpoint");
    return ResponseEntity.ok(result);
  }

  @GetMapping("/client/{id}")
  public ResponseEntity<List<AccountResponseDto>> getAccountsByClientId(@PathVariable Long id) {
    log.debug("Starting getAccountsByClientId endpoint");
    List<AccountResponseDto> result = accountService.getAccountsByClientId(id);
    log.debug("Finishing getAccountsByClientId endpoint");
    return ResponseEntity.ok(result);
  }

  @GetMapping("/{id}")
  public ResponseEntity<AccountResponseDto> getAccountById(@PathVariable Long id) {
    log.debug("Starting getAccountById endpoint");
    AccountResponseDto result = accountService.getAccountById(id);
    log.debug("Finishing getAccountById endpoint");
    return ResponseEntity.ok(result);
  }

  @GetMapping("/number/{accountNumber}")
  public ResponseEntity<AccountResponseDto> getAccountByAccountNumber(@PathVariable Long accountNumber) {
    log.debug("Starting getAccountByAccountNumber endpoint");
    AccountResponseDto result = accountService.findAccountDtoByAccountNumber(accountNumber);
    log.debug("Finishing getAccountByAccountNumber endpoint");
    return ResponseEntity.ok(result);
  }

  @PostMapping
  public ResponseEntity<AccountResponseDto> saveAccount(@Validated(Create.class) @RequestBody AccountRequestDto account) {
    log.debug("Starting saveAccount endpoint");
    AccountResponseDto result = accountService.saveAccount(account);
    log.debug("Finishing saveAccount endpoint");
    return new ResponseEntity<>(result, HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<AccountResponseDto> updateAccount(@PathVariable Long id,
      @Validated(Update.class) @RequestBody AccountRequestDto account) {
    log.debug("Starting updateAccount endpoint");
    AccountResponseDto result = accountService.updateAccount(id, account);
    log.debug("Finishing updateAccount endpoint");
    return ResponseEntity.ok(result);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
    log.debug("Starting deleteAccount endpoint");
    accountService.deleteAccount(id);
    log.debug("Finishing deleteAccount endpoint");
    return ResponseEntity.noContent().build();
  }

}
