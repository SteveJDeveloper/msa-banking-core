package com.pichincha.dm.domain.dto.requests;

import com.pichincha.dm.domain.dto.validation.Create;
import com.pichincha.dm.domain.dto.validation.Update;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdatePasswordDto {

  @NotNull(message = "La contraseña es requerida")
  @Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{10,}$", message = "La contraseña debe tener al menos 10 caracteres de longitud, una mayúscula, una minúscula, un dígito y un caracter especial", groups = {Create.class, Update.class})
  private String password;

}
