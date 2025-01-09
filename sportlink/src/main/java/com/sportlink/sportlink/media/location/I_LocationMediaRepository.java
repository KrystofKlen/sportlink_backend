package com.sportlink.sportlink.media.location;

import java.util.Optional;

public interface I_LocationMediaRepository {
    LocationMedia save(LocationMedia locationMedia);
    Optional<LocationMedia> findById(Long id);
}
