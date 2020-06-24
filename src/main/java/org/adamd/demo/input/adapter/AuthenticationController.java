package org.adamd.demo.input.adapter;

import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.adamd.demo.domain.CustomerEntity;
import org.adamd.demo.input.LoginRequestTO;
import org.adamd.demo.input.port.LoginUseCase;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Handles login calls
 * <p>
 * TODO: Error handling across calls.
 */
@Slf4j
@AllArgsConstructor
@RestController
public class AuthenticationController {

    private LoginUseCase loginUseCase;

    @ApiOperation(
        value = "Login and retrieve userinfo",
        notes = "Simply put in username and password. Test username is 'test user'"
                    + "or `test user2`, password is empty. The first test user has one rental")
    @PostMapping(
        value = "/auth",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    private Mono<CustomerEntity> login(@RequestBody LoginRequestTO loginRequestTO) {
        log.debug("user authenicated: {}", loginRequestTO.toString());

        return Mono.just(
            loginUseCase.login(loginRequestTO.getUsername(), loginRequestTO.getPassword()));
    }
}
