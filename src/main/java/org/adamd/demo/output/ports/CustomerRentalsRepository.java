package org.adamd.demo.output.ports;

import org.adamd.demo.domain.CustomerEntity;
import org.adamd.demo.domain.CustomerRentalsEntity;
import org.adamd.demo.domain.MovieEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRentalsRepository extends JpaRepository<CustomerRentalsEntity, Long> {

    List<CustomerRentalsEntity> findAllByCustomer(CustomerEntity customerEntity);

    List<CustomerRentalsEntity> findAllByCustomerAndDateReturnedIsNull(
        CustomerEntity customerEntity);
}
