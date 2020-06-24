package org.adamd.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import org.adamd.demo.domain.MovieEntity;
import org.adamd.demo.domain.MovieTypeEntity;
import org.adamd.demo.domain.MovieTypeEntity.Type;
import org.adamd.demo.input.adapter.MovieRentalService;
import org.adamd.demo.output.ports.RentalPersistencePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RentMovieUsecaseUnitTest {

    @Mock
    private RentalPersistencePort rentalPersistencePort;

    @InjectMocks
    private MovieRentalService rentMoviesUseCase;

    private static final BigDecimal NEWRELEASE_CHARGE = BigDecimal.valueOf(40.00);
    private static final MovieTypeEntity newReleasType = MovieTypeEntity
                                                             .builder()
                                                             .id(Type.NEW_RELEASE.getId())
                                                             .type(Type.NEW_RELEASE.name())
                                                             .price(NEWRELEASE_CHARGE)
                                                             .build();
    private static final BigDecimal REGULAR_CHARGE = BigDecimal.valueOf(30.00);
    private static final MovieTypeEntity regularType = MovieTypeEntity
                                                           .builder()
                                                           .id(Type.REGULAR.getId())
                                                           .type(Type.REGULAR.name())
                                                           .price(REGULAR_CHARGE)
                                                           .build();
    private static final BigDecimal OLD_CHARGE = BigDecimal.valueOf(30.00);
    private static final MovieTypeEntity oldType = MovieTypeEntity
                                                       .builder()
                                                       .id(Type.OLD.getId())
                                                       .type(Type.OLD.name())
                                                       .price(OLD_CHARGE)
                                                       .build();

    @Test
    void when_sameDayRental_shouldReturn_1DayCharge() {
        // Arrange
        final LocalDate now = LocalDate.now();

        // Act
        final BigDecimal charge = rentMoviesUseCase.calculateCharge(newReleasType, now, now);

        // Assert
        assertTrue(charge.compareTo(NEWRELEASE_CHARGE) == 0);
    }

    @Test
    void when_5DayRentalOfNewRelease_shouldReturn_5DayCharge() {
        // Arrange
        final LocalDate past = LocalDate.now().minus(5, ChronoUnit.DAYS);
        final LocalDate now = LocalDate.now();
        final BigDecimal expectedCharge = NEWRELEASE_CHARGE.multiply(BigDecimal.valueOf(5));
        // Act
        final BigDecimal charge = rentMoviesUseCase.calculateCharge(newReleasType, past, now);

        // Assert
        assertEquals(expectedCharge, charge);
    }

    @Test
    void when_2DayRentalOfRegular_shouldReturn_1DayCharge() {
        // Arrange
        final LocalDate past = LocalDate.now().minus(2, ChronoUnit.DAYS);
        final LocalDate now = LocalDate.now();

        // Act
        final BigDecimal charge = rentMoviesUseCase.calculateCharge(regularType, past, now);

        // Assert
        assertEquals(REGULAR_CHARGE, charge);
    }

    @Test
    void when_5DayRentalOfRegular_shouldReturn_2DayCharge() {
        // Arrange
        final LocalDate past = LocalDate.now().minus(5, ChronoUnit.DAYS);
        final LocalDate now = LocalDate.now();
        final BigDecimal expectedCharge = REGULAR_CHARGE.multiply(BigDecimal.valueOf(3));
        // Act
        final BigDecimal charge = rentMoviesUseCase.calculateCharge(regularType, past, now);

        // Assert
        assertEquals(expectedCharge, charge);
    }

    @Test
    void when_4DayRentalOfOld_shouldReturn_1DayCharge() {
        // Arrange
        final LocalDate past = LocalDate.now().minus(4, ChronoUnit.DAYS);
        final LocalDate now = LocalDate.now();

        // Act
        final BigDecimal charge = rentMoviesUseCase.calculateCharge(oldType, past, now);

        // Assert
        assertEquals(OLD_CHARGE, charge);
    }

    @Test
    void when_6DayRentalOfOld_shouldReturn_2DayCharge() {
        // Arrange
        final LocalDate past = LocalDate.now().minus(6, ChronoUnit.DAYS);
        final LocalDate now = LocalDate.now();
        final BigDecimal expectedCharge = OLD_CHARGE.multiply(BigDecimal.valueOf(2));
        // Act
        final BigDecimal charge = rentMoviesUseCase.calculateCharge(oldType, past, now);

        // Assert
        assertEquals(expectedCharge, charge);
    }


    @Test
    void when_RentalOfOld_shouldReturn_1BonusPoint() {
        // Arrange
        final Integer expectedPoints = 1;
        final List<MovieEntity> movieEntities = Arrays.asList(MovieEntity
                                                                  .builder()
                                                                  .movieType(oldType)
                                                                  .build());

        // Act
        final Integer bonusPoints = rentMoviesUseCase.calculateBonusPoints(movieEntities);

        // Assert
        assertEquals(expectedPoints, bonusPoints);
    }

    @Test
    void when_RentalOfRegular_shouldReturn_1BonusPoint() {
        // Arrange
        final Integer expectedPoints = 1;
        final List<MovieEntity> movieEntities = Arrays.asList(MovieEntity
                                                                  .builder()
                                                                  .movieType(regularType)
                                                                  .build());

        // Act
        final Integer bonusPoints = rentMoviesUseCase.calculateBonusPoints(movieEntities);

        // Assert
        assertEquals(expectedPoints, bonusPoints);
    }

    @Test
    void when_RentalOfNew_shouldReturn_2BonusPoint() {
        // Arrange
        final Integer expectedPoints = 2;
        final List<MovieEntity> movieEntities = Arrays.asList(MovieEntity
                                                                  .builder()
                                                                  .movieType(newReleasType)
                                                                  .build());

        // Act
        final Integer bonusPoints = rentMoviesUseCase
                                        .calculateBonusPoints(movieEntities);

        // Assert
        assertEquals(expectedPoints, bonusPoints);
    }
}
