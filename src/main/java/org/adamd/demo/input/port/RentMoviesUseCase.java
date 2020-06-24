package org.adamd.demo.input.port;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import org.adamd.demo.domain.CustomerEntity;
import org.adamd.demo.domain.CustomerRentalsEntity;
import org.adamd.demo.domain.MovieEntity;
import org.adamd.demo.domain.MovieTypeEntity;
import org.adamd.demo.domain.RentalCharge;


public interface RentMoviesUseCase {

    List<MovieEntity> browseMovies();

    Integer getBonusPoints(CustomerEntity customer);

    Integer calculateBonusPoints(List<MovieEntity> movies);

    RentalCharge rentMovies(CustomerEntity customer, List<MovieEntity> movies,
        LocalDate promisedReturnDate);

    RentalCharge returnMovies(CustomerEntity customer, List<MovieEntity> movies);

    List<CustomerRentalsEntity> getRentedMovies(CustomerEntity customer);

    BigDecimal calculateCharge(MovieTypeEntity movieTypeEntity, LocalDate rentStart,
        LocalDate rentEnd);
}
