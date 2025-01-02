package com.sportlink.sportlink.balance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface JPA_Balance_Repository extends JpaRepository<Balance, Long> {

    @Query("SELECT c FROM Currency c WHERE c.name = :name")
    Optional<Currency> findByName(@Param("name") CURRENCY name);

}
