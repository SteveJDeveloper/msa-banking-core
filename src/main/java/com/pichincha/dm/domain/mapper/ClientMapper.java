package com.pichincha.dm.domain.mapper;

import com.pichincha.dm.domain.Client;
import com.pichincha.dm.domain.dto.requests.ClientRequestDto;
import com.pichincha.dm.domain.dto.responses.ClientResponseDto;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface ClientMapper {

  @Mapping(target = "state", source = "enabled")
  ClientResponseDto toResponseDto(Client client);

  @Mapping(target = "enabled", source = "state")
  Client clientDtoToClient(ClientRequestDto clientRequestDto);

}

