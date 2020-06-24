package org.adamd.demo.output.ports;

import org.adamd.demo.domain.CustomerEntity;

public interface CustomerPersistencePort {

    CustomerEntity getUser(String username);

    CustomerEntity getUser(Long id);

    CustomerEntity incrementBonusPoints(CustomerEntity customerEntity, int bonusPoints);
}
