package io.github.dziodzi.repository;

import io.github.dziodzi.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {

    @Query("SELECT p FROM Place p LEFT JOIN FETCH p.events WHERE p.id = :id")
    Optional<Place> findById(@Param("id") long id);

    boolean existsByName(String name);

    Optional<Place> findByName(String name);
}
