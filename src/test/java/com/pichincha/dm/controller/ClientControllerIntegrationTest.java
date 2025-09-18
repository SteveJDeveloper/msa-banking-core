package com.pichincha.dm.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pichincha.dm.BaseIntegrationTest;
import com.pichincha.dm.domain.Client;
import com.pichincha.dm.domain.dto.requests.ClientRequestDto;
import com.pichincha.dm.domain.dto.requests.UpdatePasswordDto;
import com.pichincha.dm.domain.enums.Gender;
import com.pichincha.dm.repository.ClientRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
class ClientControllerIntegrationTest extends BaseIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ClientRepository clientRepository;

  @Autowired
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    clientRepository.deleteAll();
  }

  private ClientRequestDto createSampleRequest() {
    ClientRequestDto dto = new ClientRequestDto();
    dto.setName("Juan Perez");
    dto.setGender(Gender.MASCULINO);
    dto.setAge(30);
    dto.setIdentification("1234567890");
    dto.setAddress("Av. Siempre Viva 123");
    dto.setPhone("098765432");
    dto.setPassword("Test@12345");
    dto.setState(true);
    return dto;
  }

  @Test
  void testCreateClient() throws Exception {
    var request = createSampleRequest();

    mockMvc.perform(post("/api/client")
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").isNumber())
        .andExpect(jsonPath("$.name").value("Juan Perez"));
  }

  @Test
  void testGetClients() throws Exception {
    // Given
    ClientRequestDto request = createSampleRequest();
    String content = objectMapper.writeValueAsString(request);

    mockMvc.perform(post("/api/client")
        .contentType(APPLICATION_JSON)
        .content(content)).andExpect(status().isCreated());

    // When / Then
    mockMvc.perform(get("/api/client"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1));
  }

  @Test
  void testGetClientById() throws Exception {
    ClientRequestDto dto = createSampleRequest();

    String json = objectMapper.writeValueAsString(dto);
    String response = mockMvc.perform(post("/api/client")
            .contentType(APPLICATION_JSON)
            .content(json))
        .andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsString();

    long clientId = objectMapper.readTree(response).get("id").asLong();

    mockMvc.perform(get("/api/client/" + clientId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Juan Perez"));
  }

  @Test
  void testUpdateClient() throws Exception {
    ClientRequestDto dto = createSampleRequest();
    long id = objectMapper.readTree(
        mockMvc.perform(post("/api/client")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andReturn().getResponse().getContentAsString()
    ).get("id").asLong();

    dto.setName("Updated Name");
    dto.setState(true);

    mockMvc.perform(put("/api/client/" + id)
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Updated Name"));
  }

  @Test
  void testUpdatePassword() throws Exception {
    ClientRequestDto dto = createSampleRequest();
    long id = objectMapper.readTree(
        mockMvc.perform(post("/api/client")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andReturn().getResponse().getContentAsString()
    ).get("id").asLong();

    UpdatePasswordDto pwdDto = new UpdatePasswordDto();
    pwdDto.setPassword("NewPass@1234");

    mockMvc.perform(patch("/api/client/" + id)
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(pwdDto)))
        .andExpect(status().isNoContent());
  }

  @Test
  void testDeleteClient() throws Exception {
    ClientRequestDto dto = createSampleRequest();
    Long id = objectMapper.readTree(
        mockMvc.perform(post("/api/client")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andReturn().getResponse().getContentAsString()
    ).get("id").asLong();

    mockMvc.perform(delete("/api/client/" + id))
        .andExpect(status().isNoContent());

    Optional<Client> deletedClient = clientRepository.findById(id);
    assertThat(deletedClient).isEmpty();
  }
}
