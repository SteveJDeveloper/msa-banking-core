package com.pichincha.dm.domain.dto.requests;

import com.pichincha.dm.domain.dto.validation.Create;
import com.pichincha.dm.domain.dto.validation.Update;
import com.pichincha.dm.domain.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientRequestDto {

  @NotBlank(message = "El nombre es requerido", groups = {Create.class, Update.class})
  @Size(max = 100, message = "El nombre no puede tener más de 100 caracteres")
  private String name;

  @NotNull(message = "El género es requerido", groups = {Create.class, Update.class})
  private Gender gender;

  @NotNull(message = "La edad es requerida", groups = {Create.class, Update.class})
  @Positive(message = "La edad debe ser mayor a cero", groups = {Create.class, Update.class})
  private Integer age;

  @NotNull(message = "La identificación es requerida", groups = {Create.class, Update.class})
  @Pattern(regexp = "^\\d{10}$", message = "La identificación debe ser completamente numérica con 10 dígitos")
  private String identification;

  @NotBlank(message = "La dirección es requerida", groups = {Create.class, Update.class})
  @Size(max = 255, message = "La dirección no puede tener más de 255 caracteres")
  private String address;

  @NotNull(message = "El teléfono es requerido", groups = {Create.class, Update.class})
  @Pattern(regexp = "\\d+", message = "El teléfono debe ser completamente numérico", groups = {Create.class, Update.class})
  private String phone;

  @NotNull(message = "La contraseña es requerida", groups = {Create.class, Update.class})
  @Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{10,}$", message = "La contraseña debe tener al menos 10 caracteres de longitud, una mayúscula, una minúscula, un dígito y un caracter especial", groups = {Create.class, Update.class})
  private String password;

  @NotNull(message = "El estado es requerido", groups = Update.class)
  private Boolean state;

}

