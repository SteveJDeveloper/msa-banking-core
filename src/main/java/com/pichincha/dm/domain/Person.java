package com.pichincha.dm.domain;

import com.pichincha.dm.domain.enums.Gender;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@MappedSuperclass
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Person {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "nombre", nullable = false)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(name = "genero", nullable = false)
  private Gender gender;

  @Column(name = "edad", nullable = false)
  private Integer age;

  @Column(name = "identificacion", unique = true, nullable = false)
  private String identification;

  @Column(name = "direccion", nullable = false)
  private String address;

  @Column(name = "telefono", nullable = false)
  private String phone;

}
