package org.adamd.demo.input.adapter;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.adamd.demo.domain.CustomerEntity;
import org.adamd.demo.domain.CustomerRentalsEntity;
import org.adamd.demo.domain.MovieEntity;
import org.adamd.demo.domain.RentalCharge;
import org.adamd.demo.input.RentalRequestTO;
import org.adamd.demo.input.ReturnRentalRequestTO;
import org.adamd.demo.input.port.LoginUseCase;
import org.adamd.demo.input.port.RentMoviesUseCase;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Rest controller for rentals.
 * <p>
 * TODO: Error handling across calls. Journey of the user involves:
 * <ul>
 * <li>Logging in.</li>
 * <li>Browsing available movies.</li>
 * <li>Renting some movies.</li>
 * <li>Paying rent charges.</li>
 * <li>Returning some movies.</li>
 * <li>Paying late returns surcharges.</li>
 * <li>Checking bonus points</li>
 * <li>Checking un-returned rentals.</li>
 * </ul>
 */
@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/")
public class RentalController {

    private RentMoviesUseCase rentMoviesUseCase;
    private LoginUseCase loginUseCase;

    @ApiOperation(
        value = "Get List of rentable movies.",
        notes = "These are movies which have more than 1 item in the inventory.")
    @GetMapping(
        value = "/movies",
        produces = MediaType.APPLICATION_JSON_VALUE)
    private Flux<MovieEntity> getAllRentals() {
        log.debug("getAllRentals Called");
        return Flux.fromIterable(rentMoviesUseCase.browseMovies());
    }

    @ApiOperation(
        value = "Get List of rented movies to be returned by the customer.",
        notes = "These are movies without a return date.")
    @GetMapping(
        value = "/customer/{id}/rentals",
        produces = MediaType.APPLICATION_JSON_VALUE)
    private Flux<CustomerRentalsEntity> getCustomerRentals(
        @PathVariable Long id) {
        log.debug("getCustomerRentals Called");

        return Flux.fromIterable(
            rentMoviesUseCase.getRentedMovies(loginUseCase.getCustomerData(id)));
    }

    @ApiOperation(
        value = "Get a count of the bonus points.",
        notes = "Bonus points accumulated whenever a user returns a rented movie.")
    @GetMapping(
        value = "/customer/{id}/bonuspoints",
        produces = MediaType.APPLICATION_JSON_VALUE)
    private Mono<Integer> getCusomterBonusPoints(
        @PathVariable Long id) {
        log.debug("getCusomterBonusPoints Called");

        return Mono.just(
            rentMoviesUseCase.getBonusPoints(loginUseCase.getCustomerData(id)));
    }

    @ApiOperation(
        value = "Return a list of Movies.",
        notes =
            "These movies will be returned to their inventories and the user will be charged late fees"
                + "if any. Also user will be awarded bonuspoints.")
    @PutMapping(
        value = "/rental",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    private Mono<RentalCharge> closeRental(
        @RequestBody ReturnRentalRequestTO returnRentalRequestTO) {
        log.debug("rentMovies Called with body: {}", returnRentalRequestTO.toString());

        return Mono.just(
            rentMoviesUseCase.returnMovies(
                returnRentalRequestTO.getCustomerEntity(),
                returnRentalRequestTO.getMovies()));
    }

    @ApiOperation(
        value = "rent a list of Movies.",
        notes = "These movies will be removed from their inventories and the user will be charged rental fees.")
    @PostMapping(
        value = "/rental",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    private Mono<RentalCharge> rentMovies(@RequestBody RentalRequestTO rentalRequestTO) {
        log.debug("rentMovies Called with body: {}", rentalRequestTO.toString());

        return Mono.just(
            rentMoviesUseCase.rentMovies(
                rentalRequestTO.getCustomerEntity(),
                rentalRequestTO.getMovies(),
                rentalRequestTO.getDatePromised()));
    }
}
