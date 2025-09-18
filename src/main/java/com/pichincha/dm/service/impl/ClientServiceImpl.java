package com.pichincha.dm.service.impl;

import com.pichincha.dm.domain.Client;
import com.pichincha.dm.domain.dto.requests.ClientRequestDto;
import com.pichincha.dm.domain.dto.requests.UpdatePasswordDto;
import com.pichincha.dm.domain.dto.responses.ClientResponseDto;
import com.pichincha.dm.domain.mapper.ClientMapper;
import com.pichincha.dm.exception.DisabledClientException;
import com.pichincha.dm.exception.DuplicateResourceException;
import com.pichincha.dm.exception.ResourceNotFoundException;
import com.pichincha.dm.repository.ClientRepository;
import com.pichincha.dm.service.ClientService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

  private final ClientRepository clientRepository;

  private final ClientMapper clientMapper;

  @Override
  @Transactional(readOnly = true)
  public List<ClientResponseDto> getClients() {
    return clientRepository.findAll().stream()
        .map(clientMapper::toResponseDto)
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public ClientResponseDto getClientById(Long id) {
    Client client = findById(id);
    return clientMapper.toResponseDto(client);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public ClientResponseDto saveClient(ClientRequestDto clientRequestDto) {
    if (clientRepository.existsByIdentification(clientRequestDto.getIdentification())) {
      throw new DuplicateResourceException("Identificación ya existe");
    }
    Client client = clientMapper.clientDtoToClient(clientRequestDto);
    client.setEnabled(true);
    client.setPassword(securePassword(clientRequestDto.getPassword()));
    return clientMapper.toResponseDto(clientRepository.save(client));
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public ClientResponseDto updateClient(Long id, ClientRequestDto clientRequestDto) {
    Client client = findById(id);
    if (!client.getIdentification().equals(clientRequestDto.getIdentification())
        && clientRepository.existsByIdentification(clientRequestDto.getIdentification())) {
      throw new DuplicateResourceException("Identificación ya existe con otro cliente");
    }
    client.setAddress(clientRequestDto.getAddress());
    client.setGender(clientRequestDto.getGender());
    client.setAge(clientRequestDto.getAge());
    client.setIdentification(clientRequestDto.getIdentification());
    client.setPhone(clientRequestDto.getPhone());
    client.setName(clientRequestDto.getName());
    client.setPassword(securePassword(clientRequestDto.getPassword()));
    client.setEnabled(clientRequestDto.getState());
    client = clientRepository.save(client);
    return clientMapper.toResponseDto(client);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void updateClientPassword(Long id, UpdatePasswordDto updatePasswordDto) {
    Client client = findById(id);
    if (!client.isEnabled()) {
      throw new DisabledClientException("Cliente con id " + id + " está deshabilitado");
    }
    client.setPassword(securePassword(updatePasswordDto.getPassword()));
    clientRepository.save(client);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteClient(Long id) {
    Client client = findById(id);
    clientRepository.delete(client);
  }

  @Override
  @Transactional(readOnly = true)
  public Client findById(Long id) {
    return clientRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + id));
  }

  private String securePassword(String rawPassword) {
    return DigestUtils.sha3_256Hex(rawPassword);
  }

}
