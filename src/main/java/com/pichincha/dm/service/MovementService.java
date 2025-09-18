package com.pichincha.dm.service;

import com.pichincha.dm.domain.dto.requests.MovementRequestDto;
import com.pichincha.dm.domain.dto.responses.MovementResponseDto;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface MovementService {

  List<MovementResponseDto> getMovements(LocalDateTime from, LocalDateTime to);

  List<MovementResponseDto> getMovementsByAccountId(Long accountId, LocalDateTime from, LocalDateTime to);

  MovementResponseDto getMovementById(Long id);

  MovementResponseDto saveMovement(MovementRequestDto movement);

  MovementResponseDto updateMovement(Long id, MovementRequestDto movement);

  void deleteMovement(Long id);
}
