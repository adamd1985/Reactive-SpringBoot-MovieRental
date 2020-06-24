package org.adamd.demo.output;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.adamd.demo.domain.CustomerEntity;
import org.adamd.demo.output.ports.CustomerPersistencePort;
import org.adamd.demo.output.ports.CustomerRepository;
import org.springframework.stereotype.Component;

/**
 * Adapts DB operations for customer use cases.
 */
@AllArgsConstructor
@Component
@Transactional
public class CustomerPersistenceAdapter implements CustomerPersistencePort {

    private CustomerRepository customerRepository;

    @Override
    public CustomerEntity getUser(String username) {
        return customerRepository.findByName(username).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public CustomerEntity getUser(Long id) {
        return customerRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public CustomerEntity incrementBonusPoints(CustomerEntity customerEntity, int bonusPoints) {
        // TODO: Locking will need to be done here a  DB level or provide a solution that is eventual consistent.
        customerEntity.setBonusPoints(customerEntity.getBonusPoints() + bonusPoints);
        return customerRepository.save(customerEntity);
    }
}
