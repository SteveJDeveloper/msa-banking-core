package com.pichincha.dm.domain.dto.responses;

import com.pichincha.dm.domain.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientResponseDto {

  private Long id;

  private String name;

  private Gender gender;

  private Integer age;

  private String identification;

  private String address;

  private String phone;

  private String password;

  private Boolean state;

}

