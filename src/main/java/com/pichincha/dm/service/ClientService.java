package com.pichincha.dm.service;

import com.pichincha.dm.domain.Client;
import com.pichincha.dm.domain.dto.requests.ClientRequestDto;
import com.pichincha.dm.domain.dto.requests.UpdatePasswordDto;
import com.pichincha.dm.domain.dto.responses.ClientResponseDto;
import java.util.List;

public interface ClientService {

  List<ClientResponseDto> getClients();

  ClientResponseDto getClientById(Long id);

  ClientResponseDto saveClient(ClientRequestDto client);

  ClientResponseDto updateClient(Long id, ClientRequestDto client);

  void updateClientPassword(Long id, UpdatePasswordDto updatePasswordDto);

  void deleteClient(Long id);

  Client findById(Long id);

}
