package com.pichincha.dm.repository;

import com.pichincha.dm.domain.Account;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

  boolean existsByAccountNumber(Long accountNumber);

  Optional<Account> findByAccountNumber(Long accountNumber);

  List<Account> findByClientId(Long clientId);

}
