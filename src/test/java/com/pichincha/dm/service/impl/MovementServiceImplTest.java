package com.pichincha.dm.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.pichincha.dm.domain.Account;
import com.pichincha.dm.domain.Movement;
import com.pichincha.dm.domain.dto.requests.AccountRequestDto;
import com.pichincha.dm.domain.dto.requests.MovementRequestDto;
import com.pichincha.dm.domain.dto.responses.AccountResponseDto;
import com.pichincha.dm.domain.dto.responses.MovementResponseDto;
import com.pichincha.dm.domain.enums.MovementType;
import com.pichincha.dm.domain.mapper.AccountMapper;
import com.pichincha.dm.domain.mapper.ClientMapper;
import com.pichincha.dm.domain.mapper.MovementMapper;
import com.pichincha.dm.exception.DuplicateResourceException;
import com.pichincha.dm.exception.MovementCreationException;
import com.pichincha.dm.exception.ResourceNotFoundException;
import com.pichincha.dm.repository.MovementRepository;
import com.pichincha.dm.service.AccountService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MovementServiceImplTest {

  @InjectMocks
  private MovementServiceImpl movementService;

  @Mock
  private MovementRepository movementRepository;

  @Spy
  private MovementMapper movementMapper = Mappers.getMapper(MovementMapper.class);;

  @Mock
  private AccountService accountService;

  @Spy
  private AccountMapper accountMapper = Mappers.getMapper(AccountMapper.class);;

  private Movement movement;
  private MovementRequestDto movementRequestDto;
  private AccountRequestDto accountRequestDto;
  private AccountResponseDto accountResponseDto;

  @BeforeEach
  void setup() {
    movement = new Movement();
    movement.setId(1L);
    movement.setValue(new BigDecimal("100.00"));
    movement.setMovementType(MovementType.DEBITO);
    movement.setDate(LocalDateTime.now());
    movement.setBalance(new BigDecimal("900.00"));
    movement.setAccount(Account.builder().id(1L).enabled(true).balance(new BigDecimal("1000")).build());
    movement.getAccount().setAccountNumber(1234567890L);

    movementRequestDto = new MovementRequestDto();
    movementRequestDto.setValue(new BigDecimal("100.00"));
    movementRequestDto.setMovementType(MovementType.DEBITO);
    movementRequestDto.setAccountNumber(1234567890L);

    accountRequestDto = new AccountRequestDto();
    accountRequestDto.setAccountNumber(1234567890L);
    accountRequestDto.setBalance(new BigDecimal("1000.00"));
    accountRequestDto.setState(true);

    accountResponseDto = new AccountResponseDto();
    accountResponseDto.setAccountNumber(accountRequestDto.getAccountNumber());
    accountResponseDto.setAccountType(accountRequestDto.getAccountType());
    accountResponseDto.setState(accountRequestDto.getState());
  }

  @Test
  void getMovements_ShouldReturnList() {
    when(movementRepository.findAll()).thenReturn(List.of(movement));

    List<MovementResponseDto> result = movementService.getMovements(null, null);

    assertEquals(1, result.size());
    verify(movementRepository).findAll();
  }

  @Test
  void getMovementById_ShouldReturnDto() {
    when(movementRepository.findById(1L)).thenReturn(Optional.of(movement));

    MovementResponseDto result = movementService.getMovementById(1L);

    assertEquals(movement.getValue(), result.getValue());
    assertEquals(movement.getMovementType(), result.getMovementType());
    assertNotNull(result.getId());
  }

  @Test
  void getMovementById_ShouldThrow_WhenNotFound() {
    when(movementRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> movementService.getMovementById(1L));
  }

  @Test
  void saveMovement_ShouldSaveAndReturnDto_WhenAccountIsActiveAndHasFunds() {
    when(accountService.findByAccountNumber(any())).thenReturn(movement.getAccount());
    when(accountService.updateAccount(anyLong(), any())).thenReturn(accountResponseDto);
    when(movementRepository.save(any())).thenReturn(movement);

    MovementResponseDto result = movementService.saveMovement(movementRequestDto);

    assertNotNull(result);
    verify(movementRepository).save(any());
  }

  @Test
  void saveMovement_ShouldThrow_WhenAccountIsInactive() {
    accountRequestDto.setState(false);
    movement.getAccount().setEnabled(false);
    when(accountService.findByAccountNumber(any())).thenReturn(movement.getAccount());

    assertThrows(MovementCreationException.class, () -> movementService.saveMovement(
        movementRequestDto));
  }

  @Test
  void saveMovement_ShouldThrow_WhenInsufficientFunds() {
    movementRequestDto.setValue(new BigDecimal("2000.00")); // More than balance
    when(accountService.findByAccountNumber(any())).thenReturn(movement.getAccount());

    assertThrows(MovementCreationException.class, () -> movementService.saveMovement(
        movementRequestDto));
  }

  @Test
  void updateMovement_ShouldUpdateAndReturnDto_WhenValid() {
    MovementRequestDto newDto = new MovementRequestDto();
    newDto.setValue(new BigDecimal("100.00"));
    newDto.setMovementType(MovementType.DEBITO);
    newDto.setAccountNumber(1234567890L);

    when(movementRepository.findById(1L)).thenReturn(Optional.of(movement));
    when(accountService.updateAccount(anyLong(), any())).thenReturn(
        accountResponseDto);
    when(movementRepository.save(any())).thenReturn(movement);

    MovementResponseDto result = movementService.updateMovement(1L, newDto);

    assertEquals(newDto.getValue(), result.getValue());
    assertEquals(newDto.getMovementType(), result.getMovementType());
    assertEquals(newDto.getAccountNumber(), result.getAccountNumber());
    assertNotNull(result.getId());
    verify(movementRepository).save(any());
  }

  @Test
  void updateMovement_ShouldThrow_WhenAccountMismatch() {
    movementRequestDto.setAccountNumber(123L);
    when(movementRepository.findById(1L)).thenReturn(Optional.of(movement));

    assertThrows(DuplicateResourceException.class, () -> movementService.updateMovement(1L,
        movementRequestDto));
  }

  @Test
  void updateMovement_ShouldThrow_WhenInsufficientFunds() {
    movementRequestDto.setMovementType(MovementType.DEBITO);
    movementRequestDto.setValue(new BigDecimal("2000.00"));
    movement.setMovementType(MovementType.CREDITO);
    when(movementRepository.findById(1L)).thenReturn(Optional.of(movement));

    assertThrows(MovementCreationException.class, () -> movementService.updateMovement(1L,
        movementRequestDto));
  }

  @Test
  void deleteMovement_ShouldRevertBalanceAndDelete() {
    movement.setMovementType(MovementType.DEBITO);
    when(movementRepository.findById(1L)).thenReturn(Optional.of(movement));

    movementService.deleteMovement(1L);

    verify(accountService).updateAccount(eq(1L), any());
    verify(movementRepository).delete(movement);
  }

  @Test
  void findById_ShouldReturnMovement() {
    when(movementRepository.findById(1L)).thenReturn(Optional.of(movement));

    Movement result = movementService.findById(1L);

    assertEquals(movement, result);
  }

  @Test
  void findById_ShouldThrow_WhenNotFound() {
    when(movementRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> movementService.findById(1L));
  }
}
