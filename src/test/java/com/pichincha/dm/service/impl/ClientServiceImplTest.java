package com.pichincha.dm.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.pichincha.dm.domain.Client;
import com.pichincha.dm.domain.dto.requests.ClientRequestDto;
import com.pichincha.dm.domain.dto.requests.UpdatePasswordDto;
import com.pichincha.dm.domain.dto.responses.ClientResponseDto;
import com.pichincha.dm.domain.mapper.ClientMapper;
import com.pichincha.dm.exception.DisabledClientException;
import com.pichincha.dm.exception.DuplicateResourceException;
import com.pichincha.dm.exception.ResourceNotFoundException;
import com.pichincha.dm.repository.ClientRepository;
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
class ClientServiceImplTest {

  @Mock
  private ClientRepository clientRepository;

  @Spy
  private ClientMapper clientMapper = Mappers.getMapper(ClientMapper.class);

  @InjectMocks
  private ClientServiceImpl clientService;

  private Client client;
  private ClientRequestDto clientRequestDto;

  @BeforeEach
  void setUp() {

    client = new Client();
    client.setId(1L);
    client.setIdentification("1234567890");
    client.setEnabled(true);
    client.setPassword("hashed_password");

    clientRequestDto = new ClientRequestDto();
    clientRequestDto.setIdentification("1234567890");
    clientRequestDto.setPassword("plain_password");
    clientRequestDto.setState(true);
  }

  @Test
  void getClients_ShouldReturnClientDtoList() {
    when(clientRepository.findAll()).thenReturn(List.of(client));

    List<ClientResponseDto> result = clientService.getClients();

    assertEquals(1, result.size());
    verify(clientRepository).findAll();
  }

  @Test
  void getClientById_ShouldReturnClientDto() {
    when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

    ClientResponseDto result = clientService.getClientById(1L);

    assertEquals(1L, result.getId());
    assertEquals(client.getName(), result.getName());
    assertEquals(client.getGender(), result.getGender());
    assertEquals(client.getAge(), result.getAge());
    assertEquals(client.getIdentification(), result.getIdentification());
    assertEquals(client.getAddress(), result.getAddress());
    assertEquals(client.getPhone(), result.getPhone());
    assertEquals(client.isEnabled(), result.getState());
  }

  @Test
  void getClientById_ShouldThrowException_IfNotFound() {
    when(clientRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> clientService.getClientById(1L));
  }

  @Test
  void saveClient_ShouldSaveAndReturnClientDto() {
    when(clientRepository.existsByIdentification(clientRequestDto.getIdentification())).thenReturn(false);
    when(clientRepository.save(any(Client.class))).thenReturn(client);

    ClientResponseDto result = clientService.saveClient(clientRequestDto);

    assertEquals(1L, result.getId());
    assertEquals(client.getName(), result.getName());
    assertEquals(client.getGender(), result.getGender());
    assertEquals(client.getAge(), result.getAge());
    assertEquals(client.getIdentification(), result.getIdentification());
    assertEquals(client.getAddress(), result.getAddress());
    assertEquals(client.getPhone(), result.getPhone());
    assertEquals(client.isEnabled(), result.getState());
    verify(clientRepository).save(any(Client.class));
  }

  @Test
  void saveClient_ShouldThrowException_IfIdentificationExists() {
    when(clientRepository.existsByIdentification(clientRequestDto.getIdentification())).thenReturn(true);

    assertThrows(DuplicateResourceException.class, () -> clientService.saveClient(clientRequestDto));
  }

  @Test
  void updateClient_ShouldUpdateAndReturnClientDto() {
    when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
    when(clientRepository.save(any())).thenReturn(client);

    ClientResponseDto result = clientService.updateClient(1L, clientRequestDto);

    assertEquals(1L, result.getId());
    assertEquals(client.getName(), result.getName());
    assertEquals(client.getGender(), result.getGender());
    assertEquals(client.getAge(), result.getAge());
    assertEquals(client.getIdentification(), result.getIdentification());
    assertEquals(client.getAddress(), result.getAddress());
    assertEquals(client.getPhone(), result.getPhone());
    assertEquals(client.isEnabled(), result.getState());
  }

  @Test
  void updateClient_ShouldThrowException_IfIdentificationBelongsToAnotherClient() {
    client.setIdentification("old_identification");

    when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
    when(clientRepository.existsByIdentification(clientRequestDto.getIdentification())).thenReturn(true);

    assertThrows(DuplicateResourceException.class, () -> clientService.updateClient(1L,
        clientRequestDto));
  }

  @Test
  void updateClientPassword_ShouldUpdatePassword() {
    UpdatePasswordDto dto = new UpdatePasswordDto();
    dto.setPassword("new_password");

    when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
    when(clientRepository.save(any(Client.class))).thenReturn(client);

    clientService.updateClientPassword(1L, dto);

    verify(clientRepository).save(any(Client.class));
  }

  @Test
  void updateClientPassword_ShouldThrowException_IfClientIsDisabled() {
    client.setEnabled(false);
    UpdatePasswordDto dto = new UpdatePasswordDto();
    dto.setPassword("new_password");

    when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

    assertThrows(DisabledClientException.class, () -> clientService.updateClientPassword(1L, dto));
  }

  @Test
  void deleteClient_ShouldCallRepositoryDelete() {
    when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

    clientService.deleteClient(1L);

    verify(clientRepository).delete(client);
  }

  @Test
  void findById_ShouldReturnClient() {
    when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

    Client result = clientService.findById(1L);

    assertEquals(client, result);
  }

  @Test
  void findById_ShouldThrowException_IfNotFound() {
    when(clientRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> clientService.findById(1L));
  }

}