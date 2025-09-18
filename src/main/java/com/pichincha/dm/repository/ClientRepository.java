package com.pichincha.dm.repository;

import com.pichincha.dm.domain.Client;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

  Optional<Client> findByIdentification(String identification);

  boolean existsByIdentification(String identification);

}
