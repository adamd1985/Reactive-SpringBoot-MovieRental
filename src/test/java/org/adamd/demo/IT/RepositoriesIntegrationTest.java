package org.adamd.demo.IT;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.adamd.demo.domain.CustomerEntity;
import org.adamd.demo.domain.CustomerRentalsEntity;
import org.adamd.demo.domain.MovieEntity;
import org.adamd.demo.output.ports.CustomerRentalsRepository;
import org.adamd.demo.output.ports.CustomerRepository;
import org.adamd.demo.output.ports.MovieRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
@Transactional
public class RepositoriesIntegrationTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerRentalsRepository customerRentalsRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Test
    void smokeTest_JPAComponents_AreNotNull() {
        assertAll("All repos loaded", () -> {
            assertNotNull(customerRentalsRepository);
            assertNotNull(movieRepository);
            assertNotNull(customerRepository);
        });

    }

    @Test
    void smokeTest_CustomerSeedData_isAvailable() {
        // ARRANGE
        // ACT
        Optional<CustomerEntity> customerMO = customerRepository.findByName("test customer");

        // ASSERT
        assertFalse(customerMO.isEmpty());
        assertAll("verifying customerMO",
            () -> assertEquals(customerMO.get().getId(), 1L),
            () -> assertEquals(customerMO.get().getBonusPoints(), 0)
        );
    }

    @Test
    void smokeTest_CustomerRentalsSeedData_isAvailable() {
        // ARRANGE
        // ACT
        CustomerEntity customerEntity = customerRepository.findById(1L).get();
        List<CustomerRentalsEntity> customerRentalEntities = customerRentalsRepository
                                                                 .findAllByCustomer(customerEntity);
        // ASSERT
        assertNotNull(customerRentalEntities);
        assertFalse(customerRentalEntities.isEmpty());
    }


    @Test
    void smokeTest_MoviesSeedData_isAvailable() {
        // ARRANGE
        // ACT
        List<MovieEntity> movieEntities = movieRepository.findAll();

        // ASSERT
        assertFalse(movieEntities.isEmpty());
    }
}
