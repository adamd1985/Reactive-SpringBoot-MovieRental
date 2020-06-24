package org.adamd.demo.input.port;

import org.adamd.demo.domain.CustomerEntity;


public interface LoginUseCase {

    CustomerEntity login(String username, String password);

    CustomerEntity getCustomerData(Long id);
}
