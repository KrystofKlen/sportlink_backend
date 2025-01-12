package com.sportlink.sportlink.location;

import com.sportlink.sportlink.reward.Reward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface I_LocationRepository extends JpaRepository<Location, Long> {

    @Query("SELECT DISTINCT l from Location l WHERE :activity MEMBER OF l.activities")
    List<Location> findByActivity(@Param("activity") ACTIVITY activity);

    @Query("SELECT l from Location l WHERE abs(l.longitude - :longitude) < :deviation AND abs(l.latitude - :latitude ) < :deviation")
    List<Location> findNearbyLocations(@Param("longitude") double lon, @Param("latitude") double lat, @Param("deviation") double distance);

    @Query("SELECT l from Location l WHERE l.code = :code")
    Optional<Location> findByCode(@Param("code") String code);

    @Query("SELECT l.rewards FROM Location l WHERE l.id = :locationId")
    List<Reward> getRewardsForLocation(@Param("locationId") Long locationId);

    @Query("SELECT l FROM Location l WHERE l.issuer.id = :id")
    List<Location> findByIssuerId(@Param("id") Long id);
}
