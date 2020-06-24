package org.adamd.demo.input.adapter;

import lombok.AllArgsConstructor;
import org.adamd.demo.domain.CustomerEntity;
import org.adamd.demo.input.port.LoginUseCase;
import org.adamd.demo.output.ports.CustomerPersistencePort;
import org.springframework.stereotype.Component;

/**
 * Implements customer operations usecases.
 */
@AllArgsConstructor
@Component
public class CustomerService implements LoginUseCase {

    private CustomerPersistencePort customerPersistencePort;

    public CustomerEntity login(String username, String password) {
        // There would be some login logic here.
        return customerPersistencePort.getUser(username);
    }

    @Override
    public CustomerEntity getCustomerData(Long id) {
        // TODO: This would need to be secured.
        return customerPersistencePort.getUser(id);
    }
}
