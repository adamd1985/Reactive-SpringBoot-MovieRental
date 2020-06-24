package org.adamd.demo.IT;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.adamd.demo.domain.CustomerEntity;
import org.adamd.demo.domain.CustomerRentalsEntity;
import org.adamd.demo.domain.MovieEntity;
import org.adamd.demo.domain.MovieTypeEntity.Type;
import org.adamd.demo.domain.RentalCharge;
import org.adamd.demo.input.port.LoginUseCase;
import org.adamd.demo.input.port.RentMoviesUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class UseCasesIntegrationTest {

    @Autowired
    private RentMoviesUseCase rentMoviesUseCase;

    @Autowired
    private LoginUseCase loginUseCase;

    private static final String USERNAME = "test customer";
    private static final String PASSWORD = "";
    private static final LocalDate TODAY = LocalDate.now();

    @Test
    void given_customerHasAlreadyARental_when_rentAllMovies_andMoviesReturned_thenCalculate_newCharges() {
        // ARRANGE
        // User rented a standard. A new release was previously rented in the seed data..
        final BigDecimal expectedNewRentalCharges = BigDecimal.valueOf(30.00);
        final BigDecimal expectedTotalRentalCharges = BigDecimal.valueOf(70.00);
        List<MovieEntity> movies = rentMoviesUseCase.browseMovies();
        CustomerEntity customerEntity = loginUseCase.login(USERNAME, PASSWORD);
        List<CustomerRentalsEntity> prevRentals = rentMoviesUseCase.getRentedMovies(customerEntity);

        // ACT
        RentalCharge rentalCharge = rentMoviesUseCase.rentMovies(customerEntity, movies, TODAY);
        List<CustomerRentalsEntity> rentals = rentMoviesUseCase.getRentedMovies(customerEntity);
        // We return new rental and seed rental here.
        RentalCharge returnCharge = rentMoviesUseCase.returnMovies(customerEntity, movies);
        List<CustomerRentalsEntity> afterReturnRentals = rentMoviesUseCase
                                                             .getRentedMovies(customerEntity);

        final int EXPECTED_BONUSPOINTS = 3;
        CustomerEntity customerWithBonusPoints = loginUseCase.login(USERNAME, PASSWORD);

        // ASSERT
        assertAll("Assert inventory", () -> {
            assertEquals(movies.size(), 2);
            assertEquals(movies.get(0).getMovieType().getId(), Type.NEW_RELEASE.getId());
            assertEquals(movies.get(1).getMovieType().getId(), Type.REGULAR.getId());
            assertEquals(prevRentals.size(), 1);
        });

        assertAll("Assert rental charges", () -> {
            assertEquals(expectedNewRentalCharges, rentalCharge.getTotalCharges());
            assertEquals(2, rentals.size());
            // Only new rentals are counted when calculating charges.
            assertEquals(1, rentalCharge.getRentals().size());
            assertEquals(BigDecimal.valueOf(30.00), rentalCharge.getTotalCharges());
        });

        assertAll("Assert return charges", () -> {
            assertEquals(expectedTotalRentalCharges, returnCharge.getTotalCharges());
            assertEquals(BigDecimal.valueOf(0.0), returnCharge.getLateCharges());
            assertEquals(2, returnCharge.getRentals().size());
        });

        assertTrue(afterReturnRentals.isEmpty());
        assertEquals(EXPECTED_BONUSPOINTS, customerWithBonusPoints.getBonusPoints());
    }
}
