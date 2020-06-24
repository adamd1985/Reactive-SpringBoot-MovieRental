package org.adamd.demo.output.ports;

import java.util.List;
import org.adamd.demo.domain.CustomerEntity;
import org.adamd.demo.domain.CustomerRentalsEntity;
import org.adamd.demo.domain.MovieEntity;

public interface RentalPersistencePort {

    List<MovieEntity> listRentableMovies();

    List<CustomerRentalsEntity> findCustomerOpenRentals(CustomerEntity customer);

    MovieEntity incrementMovieInventory(MovieEntity movieEntity, int count);

    MovieEntity decrementMovieInventory(MovieEntity movieEntity, int count);

    List<CustomerRentalsEntity> saveOrUpdatetMovieRentals(
        List<CustomerRentalsEntity> rentalsEntities);
}
