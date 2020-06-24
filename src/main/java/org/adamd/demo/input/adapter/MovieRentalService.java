package org.adamd.demo.input.adapter;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.adamd.demo.domain.CustomerEntity;
import org.adamd.demo.domain.CustomerRentalsEntity;
import org.adamd.demo.domain.MovieEntity;
import org.adamd.demo.domain.MovieTypeEntity;
import org.adamd.demo.domain.RentalCharge;
import org.adamd.demo.input.port.RentMoviesUseCase;
import org.adamd.demo.output.ports.CustomerPersistencePort;
import org.adamd.demo.output.ports.RentalPersistencePort;
import org.springframework.stereotype.Component;

/**
 * Implements logic for rental usecases.
 */
@AllArgsConstructor
@Component
public class MovieRentalService implements RentMoviesUseCase {

    private RentalPersistencePort rentalPersistencePort;

    private CustomerPersistencePort customerPersistencePort;

    @Override
    public List<MovieEntity> browseMovies() {
        return rentalPersistencePort.listRentableMovies();
    }

    @Override
    public Integer getBonusPoints(CustomerEntity customer) {
        return customerPersistencePort.getUser(customer.getName()).getBonusPoints();
    }

    @Override
    @Transactional
    public RentalCharge rentMovies(CustomerEntity customer,
        List<MovieEntity> movies,
        LocalDate promisedReturnDate) {
        final LocalDate now = LocalDate.now();
        BigDecimal totalCharge = BigDecimal.valueOf(0);

        // Drop repeated rentals of same movie.
        final List<CustomerRentalsEntity> customerRentalsEntity =
            rentalPersistencePort.findCustomerOpenRentals(customer);

        final List<MovieEntity> uniqueMovies = movies.stream().filter(
            movieEntity -> !customerRentalsEntity.stream().anyMatch(
                rentalsEntity -> rentalsEntity.getMovie().getName()
                                     .equals(movieEntity.getName()))
        ).collect(Collectors.toList());

        List<CustomerRentalsEntity> rentalsEntities = new ArrayList<>();
        for (MovieEntity movieEntity : uniqueMovies) {
            BigDecimal charge = calculateCharge(movieEntity.getMovieType(), now,
                promisedReturnDate);
            totalCharge = totalCharge.add(charge);

            movieEntity = rentalPersistencePort.decrementMovieInventory(movieEntity, 1);

            rentalsEntities.add(CustomerRentalsEntity.builder()
                                    .customer(customer)
                                    .dateRented(now)
                                    .movie(movieEntity)
                                    .rentalCharge(charge)
                                    .build());
        }

        rentalPersistencePort.saveOrUpdatetMovieRentals(rentalsEntities);

        return RentalCharge
                   .builder()
                   .rentals(rentalsEntities)
                   .timestamp(now)
                   .totalCharges(totalCharge)
                   .build();
    }

    @Override
    public BigDecimal calculateCharge(MovieTypeEntity movieTypeEntity, LocalDate rentStart,
        LocalDate rentEnd) {

        // At least same day charge.
        final long numberOfDays =
            Math.max(1L,
                Duration.between(rentStart.atStartOfDay(), rentEnd.atStartOfDay()).toDays());
        // Surcharges
        long extraDays = 0;

        BigDecimal charge = BigDecimal.valueOf(0);

        switch (MovieTypeEntity.Type.getType(movieTypeEntity.getId())) {
            case NEW_RELEASE:
                charge = movieTypeEntity.getPrice().multiply(BigDecimal.valueOf(numberOfDays));
                break;

            case REGULAR:
                if (numberOfDays > 3) {
                    extraDays = numberOfDays - 3;
                }

                charge = movieTypeEntity.getPrice().multiply(BigDecimal.valueOf(extraDays))
                             .add(movieTypeEntity.getPrice());
                break;

            case OLD:
                if (numberOfDays > 5) {
                    extraDays = numberOfDays - 5;
                }

                charge = movieTypeEntity.getPrice().multiply(BigDecimal.valueOf(extraDays))
                             .add(movieTypeEntity.getPrice());
                break;
        }

        return charge;
    }

    @Override
    public Integer calculateBonusPoints(List<MovieEntity> movies) {
        Integer bonusPoints = 0;

        for (MovieEntity movie : movies) {
            switch (MovieTypeEntity.Type.getType(movie.getMovieType().getId())) {
                case NEW_RELEASE:
                    bonusPoints += 2;
                    break;

                case REGULAR:
                case OLD:
                    bonusPoints += 1;
                    break;
            }
        }

        return bonusPoints;
    }

    @Override
    @Transactional
    public RentalCharge returnMovies(CustomerEntity customer, List<MovieEntity> movies) {
        List<CustomerRentalsEntity> openRentals = rentalPersistencePort
                                                      .findCustomerOpenRentals(customer);

        List<CustomerRentalsEntity> rentalsToClose = openRentals.stream().filter(
            customerRentalsEntity ->
                movies.stream().anyMatch(movieEntity -> movieEntity.getName().equals(
                    customerRentalsEntity.getMovie().getName()))).collect(Collectors.toList());

        LocalDate returnDate = LocalDate.now();
        BigDecimal totalCharges = BigDecimal.valueOf(0.00);
        BigDecimal lateCharges = BigDecimal.valueOf(0.00);

        for (CustomerRentalsEntity rentalsEntity : rentalsToClose) {
            final BigDecimal charge =
                calculateCharge(rentalsEntity.getMovie().getMovieType(),
                    rentalsEntity.getDateRented(),
                    returnDate);

            if (rentalsEntity.getRentalCharge() != null) {
                lateCharges = lateCharges.add(charge.subtract(rentalsEntity.getRentalCharge()));
            }
            totalCharges = totalCharges.add(charge);

            rentalsEntity.setRentalCharge(charge);
            rentalsEntity.setDateReturned(returnDate);
        }

        rentalsToClose = rentalPersistencePort.saveOrUpdatetMovieRentals(rentalsToClose);

        movies.stream().forEach((movieEntity) ->
                                    rentalPersistencePort.decrementMovieInventory(movieEntity, 1));

        final Integer bonusPoints = calculateBonusPoints(movies);
        customerPersistencePort.incrementBonusPoints(customer, bonusPoints);

        return RentalCharge
                   .builder()
                   .rentals(rentalsToClose)
                   .timestamp(returnDate)
                   .totalCharges(totalCharges)
                   .lateCharges(lateCharges)
                   .build();
    }

    public List<CustomerRentalsEntity> getRentedMovies(CustomerEntity customer) {
        return rentalPersistencePort.findCustomerOpenRentals(customer);
    }
}
