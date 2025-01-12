package com.sportlink.sportlink.visit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface I_VisitRepository extends JpaRepository<Visit, Long> {

    @Query("SELECT v FROM Visit v WHERE v.visitor.id =: id")
    List<Visit> getVisitsForUser(@Param("id") Long id);

    @Query("SELECT v FROM Visit v WHERE v.location.id =: companyId")
    List<Visit> getVisitsForCompany(@Param("companyId") Long companyId);

    @Query("SELECT v FROM Visit v WHERE v.visitor.id = :visitorId ORDER BY v.timestampStart DESC")
    Optional<Visit> getLastByVisitorId(@Param("visitorId") Long visitorId);
}
