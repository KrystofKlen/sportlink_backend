package com.sportlink.sportlink.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface I_ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT r FROM Review r WHERE r.location.id =: locationId")
    List<Review> findAllForLocation(@Param("locationId") long locationId);
}
