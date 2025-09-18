package com.pichincha.dm.repository;

import com.pichincha.dm.domain.Movement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MovementRepository extends JpaRepository<Movement, Long> {

  List<Movement> findByAccountId(Long accountId);

  @Query(value = """
      SELECT m FROM Movement m JOIN FETCH m.account
      WHERE m.date BETWEEN :from AND :to
      """)
  List<Movement> findByDateRange(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

  @Query(value = """
      SELECT m FROM Movement m JOIN FETCH m.account
      WHERE m.date >= :from
      """)
  List<Movement> findByFromDate(@Param("from") LocalDateTime from);

  @Query(value = """
      SELECT m FROM Movement m JOIN FETCH m.account
      WHERE m.date <= :to
      """)
  List<Movement> findByToDate(@Param("from") LocalDateTime to);

  @Query(value = """
      SELECT m FROM Movement m JOIN FETCH m.account
      WHERE m.account.id = :accountId AND m.date BETWEEN :from AND :to
      """)
  List<Movement> findByAccountAndDateRange(@Param("accountId") Long accountId,
      @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

  @Query(value = """
      SELECT m FROM Movement m JOIN FETCH m.account
      WHERE m.account.id = :accountId AND m.date >= :from
      """)
  List<Movement> findByAccountAndFromDate(@Param("accountId") Long accountId,
      @Param("from") LocalDateTime from);

  @Query(value = """
      SELECT m FROM Movement m JOIN FETCH m.account
      WHERE m.account.id = :accountId AND m.date <= :to
      """)
  List<Movement> findByAccountAndToDate(@Param("accountId") Long accountId,
      @Param("from") LocalDateTime to);

}
