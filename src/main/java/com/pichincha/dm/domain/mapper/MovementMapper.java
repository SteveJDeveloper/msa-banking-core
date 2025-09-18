package com.pichincha.dm.domain.mapper;

import com.pichincha.dm.domain.Movement;
import com.pichincha.dm.domain.dto.requests.MovementRequestDto;
import com.pichincha.dm.domain.dto.responses.MovementResponseDto;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true), unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface MovementMapper {

  @Mapping(target = "accountNumber", source = "account.accountNumber")
  MovementResponseDto toResponseDto(Movement movement);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "creationDate", ignore = true)
  @Mapping(target = "updateDate", ignore = true)
  @Mapping(target = "account", ignore = true)
  @Mapping(target = "date", ignore = true)
  @Mapping(target = "balance", ignore = true)
  Movement movementDtoToMovement(MovementRequestDto movementRequestDto);
}
