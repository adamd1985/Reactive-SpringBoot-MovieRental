package org.adamd.demo.output;

import java.util.List;
import javax.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.adamd.demo.domain.CustomerEntity;
import org.adamd.demo.domain.CustomerRentalsEntity;
import org.adamd.demo.domain.MovieEntity;
import org.adamd.demo.output.ports.CustomerRentalsRepository;
import org.adamd.demo.output.ports.CustomerRepository;
import org.adamd.demo.output.ports.MovieRepository;
import org.adamd.demo.output.ports.RentalPersistencePort;
import org.springframework.stereotype.Component;

/**
 * Adapts DB operations for rental usecases.
 */
@AllArgsConstructor
@Component
@Transactional
public class RentalPersistenceAdapter implements RentalPersistencePort {

    private MovieRepository movieRepository;
    private CustomerRentalsRepository customerRentalsRepository;
    private CustomerRepository customerRepository;


    @Override
    public List<MovieEntity> listRentableMovies() {
        // Omitted inventory counts for simplicity.
        return movieRepository.findAllByInventoryGreaterThan(0);
    }

    @Override
    public List<CustomerRentalsEntity> saveOrUpdatetMovieRentals(
        List<CustomerRentalsEntity> rentalsEntities) {

        return customerRentalsRepository.saveAll(rentalsEntities);
    }

    @Override
    public List<CustomerRentalsEntity> findCustomerOpenRentals(CustomerEntity customerEntity) {

        List<CustomerRentalsEntity> rentalsEntities = customerRentalsRepository
                                                          .findAllByCustomerAndDateReturnedIsNull(
                                                              customerEntity);

        return rentalsEntities;
    }

    @Override
    public MovieEntity incrementMovieInventory(MovieEntity movieEntity, int count) {
        // TODO: Locking will need to be done here a  DB level or provide a solution that is eventual consistent.
        movieEntity.setInventory(movieEntity.getInventory() + count);
        return movieRepository.save(movieEntity);
    }

    @Override
    public MovieEntity decrementMovieInventory(MovieEntity movieEntity, int count) {
        // TODO:  Locking will need to be done here a  DB level or provide a solution that is eventual consistent.
        movieEntity.setInventory(movieEntity.getInventory() - count);
        return movieRepository.save(movieEntity);
    }
}
