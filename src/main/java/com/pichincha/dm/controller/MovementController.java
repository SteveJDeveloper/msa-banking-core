package com.pichincha.dm.controller;

import com.pichincha.dm.domain.dto.requests.MovementRequestDto;
import com.pichincha.dm.domain.dto.responses.MovementResponseDto;
import com.pichincha.dm.service.MovementService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movement")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class MovementController {

  private final MovementService movementService;

  @GetMapping
  public ResponseEntity<List<MovementResponseDto>> getMovements(
      @RequestParam(required = false) @DateTimeFormat LocalDateTime from,
      @RequestParam(required = false) @DateTimeFormat LocalDateTime to) {
    log.debug("Starting getMovements endpoint");
    List<MovementResponseDto> result = movementService.getMovements(from, to);
    log.debug("Finishing getMovements endpoint");
    return ResponseEntity.ok(result);
  }

  @GetMapping("/account/{accountId}")
  public ResponseEntity<List<MovementResponseDto>> getMovementsByAccountId(@PathVariable Long accountId,
      @RequestParam(required = false) @DateTimeFormat LocalDateTime from,
      @RequestParam(required = false) @DateTimeFormat LocalDateTime to) {
    log.debug("Starting getMovementsByAccountId endpoint");
    List<MovementResponseDto> result = movementService.getMovementsByAccountId(accountId, from, to);
    log.debug("Finishing getMovementsByAccountId endpoint");
    return ResponseEntity.ok(result);
  }

  @GetMapping("/{id}")
  public ResponseEntity<MovementResponseDto> getMovementById(@PathVariable Long id) {
    log.debug("Starting getMovementById endpoint");
    MovementResponseDto result = movementService.getMovementById(id);
    log.debug("Finishing getMovementById endpoint");
    return ResponseEntity.ok(result);
  }

  @PostMapping
  public ResponseEntity<MovementResponseDto> saveMovement(@RequestBody MovementRequestDto movement) {
    log.debug("Starting saveMovement endpoint");
    MovementResponseDto result = movementService.saveMovement(movement);
    log.debug("Finishing saveMovement endpoint");
    return new ResponseEntity<>(result, HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<MovementResponseDto> updateMovement(@PathVariable Long id,
      @RequestBody MovementRequestDto movement) {
    log.debug("Starting updateMovement endpoint");
    MovementResponseDto result = movementService.updateMovement(id, movement);
    log.debug("Finishing updateMovement endpoint");
    return ResponseEntity.ok(result);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteMovement(@PathVariable Long id) {
    log.debug("Starting deleteMovement endpoint");
    movementService.deleteMovement(id);
    log.debug("Finishing deleteMovement endpoint");
    return ResponseEntity.noContent().build();
  }
}
