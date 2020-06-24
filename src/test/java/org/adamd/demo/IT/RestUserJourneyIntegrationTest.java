package org.adamd.demo.IT;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.adamd.demo.domain.CustomerEntity;
import org.adamd.demo.domain.MovieEntity;
import org.adamd.demo.domain.MovieTypeEntity.Type;
import org.adamd.demo.domain.RentalCharge;
import org.adamd.demo.input.LoginRequestTO;
import org.adamd.demo.input.RentalRequestTO;
import org.adamd.demo.input.ReturnRentalRequestTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

@SpringBootTest
@AutoConfigureWebTestClient
public class RestUserJourneyIntegrationTest {

    @Autowired
    private WebTestClient webClient;

    private static final String USERNAME = "test customer2";
    private static final String PASSWORD = "";
    private static final LocalDate TODAY = LocalDate.now();
    private static final BigDecimal REGULAR_CHARGE = BigDecimal.valueOf(30.00);
    private static final BigDecimal NEWRELEASE_CHARGE = BigDecimal.valueOf(40.00);


    @Test
    void shouldTest_userJourney_browseMovies_rentMovies_renturnMovies_andPay_expectedToSucceed() {
        LoginRequestTO loginRequestTO = LoginRequestTO
                                            .builder()
                                            .password(PASSWORD)
                                            .username(USERNAME)
                                            .build();

        CustomerEntity customerEntity = webClient
                                            .post()
                                            .uri("/auth")
                                            .body(BodyInserters.fromValue(loginRequestTO))
                                            .header(HttpHeaders.ACCEPT, "application/json")
                                            .exchange()
                                            .expectStatus().isOk()
                                            .expectBody(CustomerEntity.class)
                                            .returnResult()
                                            .getResponseBody();

        assertEquals(USERNAME, customerEntity.getName());

        List<MovieEntity> movies = webClient
                                       .get()
                                       .uri("/movies")
                                       .header(HttpHeaders.ACCEPT, "application/json")
                                       .exchange()
                                       .expectStatus().isOk()
                                       .expectBodyList(MovieEntity.class)
                                       .returnResult()
                                       .getResponseBody();

        // User rents 1 movie: Spiderman.
        assertEquals(2, movies.size());
        MovieEntity spiderMan = movies.get(1);

        assertEquals("Spiderman", spiderMan.getName());
        assertEquals(Type.REGULAR, Type.getType(spiderMan.getMovieType().getId()));

        final List<MovieEntity> moviesToRent = Arrays.asList(spiderMan);

        RentalRequestTO rentalRequestTO = RentalRequestTO
                                              .builder()
                                              .customerEntity(customerEntity)
                                              .datePromised(TODAY)
                                              .movies(moviesToRent)
                                              .timestamp(TODAY)
                                              .build();

        RentalCharge rentalCharge = webClient
                                        .post()
                                        .uri("/rental")
                                        .body(BodyInserters.fromValue(rentalRequestTO))
                                        .header(HttpHeaders.ACCEPT, "application/json")
                                        .exchange()
                                        .expectStatus().isOk()
                                        .expectBody(RentalCharge.class)
                                        .returnResult()
                                        .getResponseBody();

        assertAll("Rental verification", () -> {
            assertEquals(1, rentalCharge.getRentals().size());
            assertEquals("Spiderman", rentalCharge.getRentals().get(0).getMovie().getName());
            assertEquals(REGULAR_CHARGE, rentalCharge.getTotalCharges());
            assertNull(rentalCharge.getLateCharges());
        });

        ReturnRentalRequestTO returnRentalRequestTO = ReturnRentalRequestTO
                                                          .builder()
                                                          .customerEntity(customerEntity)
                                                          .movies(moviesToRent)
                                                          .timestamp(TODAY)
                                                          .build();

        RentalCharge returnCharge = webClient
                                        .put()
                                        .uri("/rental")
                                        .body(BodyInserters.fromValue(returnRentalRequestTO))
                                        .header(HttpHeaders.ACCEPT, "application/json")
                                        .exchange()
                                        .expectStatus().isOk()
                                        .expectBody(RentalCharge.class)
                                        .returnResult()
                                        .getResponseBody();

        assertAll("Final Return verification", () -> {
            assertEquals(1, returnCharge.getRentals().size());
            assertEquals("Spiderman", returnCharge.getRentals().get(0).getMovie().getName());
            assertEquals(REGULAR_CHARGE, returnCharge.getTotalCharges());
            assertEquals(BigDecimal.valueOf(0.00), returnCharge.getLateCharges());
        });

        Integer bonusPoints = webClient
                                  .get()
                                  .uri(uriBuilder -> uriBuilder
                                                         .path("/customer/{id}/bonuspoints")
                                                         .build(customerEntity.getId()))
                                  .header(HttpHeaders.ACCEPT, "application/json")
                                  .exchange()
                                  .expectStatus().isOk()
                                  .expectBody(Integer.class)
                                  .returnResult()
                                  .getResponseBody();

        assertEquals(1, bonusPoints);

        List<MovieEntity> remainingMoviesRented = webClient
                                                      .get()
                                                      .uri(uriBuilder -> uriBuilder
                                                                             .path(
                                                                                 "/customer/{id}/rentals")
                                                                             .build(customerEntity
                                                                                        .getId()))
                                                      .header(HttpHeaders.ACCEPT,
                                                          "application/json")
                                                      .exchange()
                                                      .expectStatus().isOk()
                                                      .expectBodyList(MovieEntity.class)
                                                      .returnResult()
                                                      .getResponseBody();

        assertTrue(remainingMoviesRented.isEmpty());
    }
}

