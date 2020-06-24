package org.adamd.demo.output.ports;

import java.util.List;
import org.adamd.demo.domain.MovieEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends JpaRepository<MovieEntity, Long> {

    MovieEntity findByName(String name);

    List<MovieEntity> findAllByNameIn(List<String> movieNames);

    List<MovieEntity> findAllByInventoryGreaterThan(int inventory);
}
