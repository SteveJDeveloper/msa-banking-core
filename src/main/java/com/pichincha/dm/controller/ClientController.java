package com.pichincha.dm.controller;

import com.pichincha.dm.domain.dto.requests.ClientRequestDto;
import com.pichincha.dm.domain.dto.requests.UpdatePasswordDto;
import com.pichincha.dm.domain.dto.responses.ClientResponseDto;
import com.pichincha.dm.domain.dto.validation.Create;
import com.pichincha.dm.domain.dto.validation.Update;
import com.pichincha.dm.service.ClientService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/client")
@RequiredArgsConstructor
@CrossOrigin
public class ClientController {

  private final ClientService clientService;

  @GetMapping
  public ResponseEntity<List<ClientResponseDto>> getClients() {
    return ResponseEntity.ok(clientService.getClients());
  }

  @GetMapping("/{id}")
  public ResponseEntity<ClientResponseDto> getClientById(@PathVariable Long id) {
    return ResponseEntity.ok(clientService.getClientById(id));
  }

  @PostMapping
  public ResponseEntity<ClientResponseDto> saveClient(@Validated(Create.class) @RequestBody ClientRequestDto client) {
    return new ResponseEntity<>(clientService.saveClient(client), HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<ClientResponseDto> updateClient(@PathVariable Long id, @Validated(Update.class) @RequestBody ClientRequestDto client) {
    return ResponseEntity.ok(clientService.updateClient(id, client));
  }

  @PatchMapping("/{id}")
  public ResponseEntity<Void> updateClientPassword(@PathVariable Long id, @Valid @RequestBody UpdatePasswordDto updatePasswordDto) {
    clientService.updateClientPassword(id, updatePasswordDto);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
    clientService.deleteClient(id);
    return ResponseEntity.noContent().build();
  }
}
