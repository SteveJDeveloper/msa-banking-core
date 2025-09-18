package com.pichincha.dm.domain.mapper;

import com.pichincha.dm.domain.Account;
import com.pichincha.dm.domain.dto.requests.AccountRequestDto;
import com.pichincha.dm.domain.dto.responses.AccountResponseDto;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface AccountMapper {

  @Mapping(target = "name", source = "client.name")
  @Mapping(target = "state", source = "enabled")
  @Mapping(target = "clientId", source = "client.id")
  AccountResponseDto toAccountResponseDto(Account account);

  @Mapping(target = "clientId", source = "client.id")
  @Mapping(target = "state", source = "enabled")
  AccountRequestDto toAccountRequestDto(Account account);

  @Mapping(target = "client", ignore = true)
  @Mapping(target = "creationDate", ignore = true)
  @Mapping(target = "updateDate", ignore = true)
  @Mapping(target = "enabled", source = "state")
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "movements", ignore = true)
  Account toEntity(AccountRequestDto accountRequestDto);

}
