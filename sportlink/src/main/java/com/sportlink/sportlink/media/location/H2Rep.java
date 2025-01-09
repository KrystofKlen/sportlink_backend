package com.sportlink.sportlink.media.location;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class H2Rep implements I_LocationMediaRepository{

    private final JpaRepository<LocationMedia, Long> repo;

    public H2Rep(JpaRepository<LocationMedia, Long> repo) {
        this.repo = repo;
    }

    @Override
    public LocationMedia save(LocationMedia locationMedia) {
        return repo.save(locationMedia);
    }

    @Override
    public Optional<LocationMedia> findById(Long id) {
        return repo.findById(id);
    }
}
