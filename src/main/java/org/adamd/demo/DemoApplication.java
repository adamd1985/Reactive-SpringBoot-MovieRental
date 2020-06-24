package org.adamd.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;

/**
 * Reactive Restful rental app.
 * <p>
 * Hexagonal architecture used: ports are interfaces for inputs and outputs. Usecases map to
 * customer actions and adapted adapt these and ports to implementation. Everything is built around
 * a domain.
 * </p>
 * Entities play a part of DTO and BO to keep code to the minimum. A normal app would have a variety
 * of DTOs to abstract data flow.
 * </p>
 * TODO: WebFlux caused H2 console to be disabled. Re-enable it.
 */
@EnableWebFlux
@SpringBootApplication(scanBasePackages = {"org.adamd.demo", "org.adamd.demo.input",
    "org.adamd.demo.output"})
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
