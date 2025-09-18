package com.pichincha.dm.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pichincha.dm.BaseIntegrationTest;
import com.pichincha.dm.domain.Account;
import com.pichincha.dm.domain.Client;
import com.pichincha.dm.domain.dto.requests.AccountRequestDto;
import com.pichincha.dm.domain.enums.AccountType;
import com.pichincha.dm.domain.enums.Gender;
import com.pichincha.dm.repository.AccountRepository;
import com.pichincha.dm.repository.ClientRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
class AccountControllerIntegrationTest extends BaseIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private ClientRepository clientRepository;

  private Long clientId;

  @BeforeEach
  void setup() {
    accountRepository.deleteAll();
    clientRepository.deleteAll();

    Client client = Client.builder()
        .password("12")
        .enabled(true)
        .build();
    client.setName("Name 1");
    client.setGender(Gender.MASCULINO);
    client.setAge(26);
    client.setIdentification("123");
    client.setAddress("Dir 1");
    client.setPhone("098");
    clientRepository.save(client);

    // Seed a client (replace with your own logic)
    client = clientRepository.save(client);

    clientId = client.getId();

    Account account = Account.builder()
        .accountNumber(123L)
        .accountType(AccountType.AHORROS)
        .balance(new BigDecimal(100))
        .enabled(true)
        .client(client)
        .build();
    Account saved = accountRepository.save(account);
  }

  @Test
  void shouldReturnAllAccounts() throws Exception {
    mockMvc.perform(get("/api/account"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1));
  }

  @Test
  void shouldReturnAccountById() throws Exception {
    var account = accountRepository.findAll().get(0);

    mockMvc.perform(get("/api/account/" + account.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accountNumber").value(account.getAccountNumber()));
  }

  @Test
  void shouldReturnAccountsByClientId() throws Exception {
    mockMvc.perform(get("/api/account/client/" + clientId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1));
  }

  @Test
  void shouldCreateAccount() throws Exception {
    AccountRequestDto request = new AccountRequestDto(1234L, AccountType.AHORROS, clientId, new BigDecimal(1000), null);
    request.setClientId(clientId);

    mockMvc.perform(post("/api/account")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.accountNumber").value(request.getAccountNumber()))
        .andExpect(jsonPath("$.state").value(true));

    assertThat(accountRepository.findAll()).hasSize(2);
  }

  @Test
  void shouldUpdateAccount() throws Exception {
    var account = accountRepository.findAll().get(0);

    AccountRequestDto request = new AccountRequestDto(1234L, AccountType.AHORROS, clientId, new BigDecimal(1000), false);
    request.setClientId(clientId);

    mockMvc.perform(put("/api/account/" + account.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accountNumber").value(request.getAccountNumber()))
        .andExpect(jsonPath("$.state").value(request.getState()));
  }

  @Test
  void shouldDeleteAccount() throws Exception {
    var account = accountRepository.findAll().get(0);

    mockMvc.perform(delete("/api/account/" + account.getId()))
        .andExpect(status().isNoContent());

    assertThat(accountRepository.findById(account.getId())).isEmpty();
  }
}
