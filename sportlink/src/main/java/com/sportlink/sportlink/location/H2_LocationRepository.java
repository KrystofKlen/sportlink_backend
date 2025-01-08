package com.sportlink.sportlink.location;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Primary
public class H2_LocationRepository implements I_LocationRepository {

    private final JPA_LocationRepository jpaRepository;
    private final EntityManager entityManager;

    @Autowired
    public H2_LocationRepository(JPA_LocationRepository jpaRepository, EntityManager entityManager) {
        this.jpaRepository = jpaRepository;
        this.entityManager = entityManager;
    }

    @Override
    public Location save(Location location) {
        return jpaRepository.save(location);
    }

    @Override
    public Optional<Location> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Location> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public List<Location> findByActivity(List<ACTIVITY> activities) {
        TypedQuery<Location> query = entityManager.createQuery(
                "SELECT l FROM Location l WHERE l.activities IN :activities", Location.class
        );
        query.setParameter("activities", activities);
        return query.getResultList();
    }

    @Override
    public List<Location> findNearbyLocations(double lon, double lat, double deviation) {
        TypedQuery<Location> query = entityManager.createQuery(
                "SELECT l FROM Location l " +
                        "WHERE abs(l.longitude - :longitude) < :deviation AND abs(l.latitude - :latitude ) < :deviation",
                Location.class
        );
        query.setParameter("latitude", lat);
        query.setParameter("longitude", lon);
        query.setParameter("deviation", deviation);
        return query.getResultList();
    }
}
