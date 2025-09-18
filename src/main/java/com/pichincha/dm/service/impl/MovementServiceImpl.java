package com.pichincha.dm.service.impl;

import com.pichincha.dm.domain.Account;
import com.pichincha.dm.domain.Movement;
import com.pichincha.dm.domain.dto.requests.AccountRequestDto;
import com.pichincha.dm.domain.dto.requests.MovementRequestDto;
import com.pichincha.dm.domain.dto.responses.AccountResponseDto;
import com.pichincha.dm.domain.dto.responses.MovementResponseDto;
import com.pichincha.dm.domain.mapper.AccountMapper;
import com.pichincha.dm.domain.mapper.MovementMapper;
import com.pichincha.dm.exception.DuplicateResourceException;
import com.pichincha.dm.exception.MovementCreationException;
import com.pichincha.dm.exception.ResourceNotFoundException;
import com.pichincha.dm.repository.MovementRepository;
import com.pichincha.dm.service.AccountService;
import com.pichincha.dm.service.MovementService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MovementServiceImpl implements MovementService {

  private final MovementRepository movementRepository;

  private final MovementMapper movementMapper;

  private final AccountService accountService;

  private final AccountMapper accountMapper;

  @Override
  @Transactional(readOnly = true)
  public List<MovementResponseDto> getMovements(LocalDateTime from, LocalDateTime to) {
    List<Movement> movements;
    if (from != null && to != null) {
      movements = movementRepository.findByDateRange(from, to);
    } else if (from != null) {
      movements = movementRepository.findByFromDate(from);
    } else if (to != null) {
      movements = movementRepository.findByToDate(to);
    } else {
      movements = movementRepository.findAll();
    }
    return movements.stream()
        .map(movementMapper::toResponseDto)
        .toList();
  }

  @Override
  public List<MovementResponseDto> getMovementsByAccountId(Long accountId, LocalDateTime from, LocalDateTime to) {
    List<Movement> movements;
    if (from != null && to != null) {
      movements = movementRepository.findByAccountAndDateRange(accountId, from, to);
    } else if (from != null) {
      movements = movementRepository.findByAccountAndFromDate(accountId, from);
    } else if (to != null) {
      movements = movementRepository.findByAccountAndToDate(accountId, to);
    } else {
      movements = movementRepository.findByAccountId(accountId);
    }
    return movements.stream()
        .map(movementMapper::toResponseDto)
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public MovementResponseDto getMovementById(Long id) {
    Movement movement = movementRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Movimiento no encontrado con id: " + id));
    return movementMapper.toResponseDto(movement);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public MovementResponseDto saveMovement(MovementRequestDto movementRequestDto) {
    Account account = accountService.findByAccountNumber(movementRequestDto.getAccountNumber());
    AccountRequestDto accountRequestDto = accountMapper.toAccountRequestDto(account);
    if (!accountRequestDto.getState()) {
      throw new MovementCreationException("La cuenta está inactiva");
    }
    switch (movementRequestDto.getMovementType()) {
      case DEBITO -> calculateDebit(accountRequestDto, movementRequestDto.getValue());
      case CREDITO -> calculateCredit(accountRequestDto, movementRequestDto.getValue());
    }
    AccountResponseDto response = accountService.updateAccount(account.getId(), accountRequestDto);
    Movement movement = movementMapper.movementDtoToMovement(movementRequestDto);
    movement.setBalance(response.getBalance());
    movement.setDate(LocalDateTime.now());
    movement.setAccount(account);
    movement = movementRepository.save(movement);
    return movementMapper.toResponseDto(movement);
  }

  private void calculateDebit(AccountRequestDto account, BigDecimal value) {
    if (account.getBalance().compareTo(value) < 0) {
      throw new MovementCreationException(
          "Saldo insuficiente en la cuenta: " + account.getAccountNumber());
    }
    account.setBalance(account.getBalance().subtract(value));
  }

  private void calculateCredit(AccountRequestDto account, BigDecimal value) {
    account.setBalance(account.getBalance().add(value));
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public MovementResponseDto updateMovement(Long id, MovementRequestDto movementRequestDto) {
    Movement movement = findById(id);
    if (!movement.getAccount().getAccountNumber().equals(movementRequestDto.getAccountNumber())) {
      throw new DuplicateResourceException("Este movimiento le pertenece a otro número de cuenta");
    }
    AccountRequestDto accountRequestDto = accountMapper.toAccountRequestDto(movement.getAccount());
    BigDecimal currentBalance = getCurrentBalance(movementRequestDto, accountRequestDto, movement);
    accountRequestDto.setBalance(currentBalance);
    accountService.updateAccount(movement.getAccount().getId(), accountRequestDto);
    BeanUtils.copyProperties(movementRequestDto, movement, "id");
    movement.setBalance(currentBalance);
    movement.setDate(LocalDateTime.now());
    movement = movementRepository.save(movement);
    return movementMapper.toResponseDto(movement);
  }

  private static BigDecimal getCurrentBalance(MovementRequestDto movementRequestDto, AccountRequestDto accountRequestDto,
      Movement existing) {
    BigDecimal currentBalance = existing.getBalance();
    switch (existing.getMovementType()) {
      case DEBITO -> currentBalance = currentBalance.add(existing.getValue());
      case CREDITO -> currentBalance = currentBalance.subtract(existing.getValue());
    }
    switch (movementRequestDto.getMovementType()) {
      case DEBITO -> {
        if (currentBalance.compareTo(movementRequestDto.getValue()) < 0) {
          throw new MovementCreationException("Saldo insuficiente para modificar el movimiento");
        }
        currentBalance = currentBalance.subtract(movementRequestDto.getValue());
      }
      case CREDITO -> currentBalance = currentBalance.add(movementRequestDto.getValue());
    }
    return currentBalance;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteMovement(Long id) {
    Movement movement = findById(id);
    AccountRequestDto accountRequestDto = accountMapper.toAccountRequestDto(movement.getAccount());
    BigDecimal balance = accountRequestDto.getBalance();
    BigDecimal value = movement.getValue();
    switch (movement.getMovementType()) {
      case DEBITO -> accountRequestDto.setBalance(balance.add(value));
      case CREDITO -> accountRequestDto.setBalance(balance.subtract(value));
    }
    accountService.updateAccount(movement.getAccount().getId(), accountRequestDto);
    movementRepository.delete(movement);
  }

  @Transactional(readOnly = true)
  public Movement findById(Long id) {
    return movementRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Movimiento no encontrado con id: " + id));
  }

}
