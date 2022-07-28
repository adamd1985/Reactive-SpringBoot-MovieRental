# Movie Rental Webserver

An industry standard reactive java spring-powered RESTful app.

For those interested, this is the 'take-home' project requested by most iGaming companies - this in particular is Casumo Tehnical Test and take home time-waster.
Don't expect much challenge from companies whose only tech is slot machines.

## Important Technical Decisions:

* Demo comes working out of the box, if requirements are satisfied: embedded database and a wrapper for build.
* Built on WebFlux to be reactive-ready. Server is stateless and microservice ready.
* Inventory and Bonus Points in persistence layer is not thread-safe. Assumption being an adapter will
make these consistent eventually in the correct microservice infrastructure.
* Did not create DTOs, MOs and BOs. Entities are used as all 3, this is not to bloat code for the sake of this 
exercise. In a market-ready app specific TOs will be used to abstract layers and communications.
* Hexagonal architecture utilized to clearly seperate actors. Components are comprised as ports, port adapters, usecases and 
domain for ins and outs.
* Rest endpoints designed with Open API 3.0 for testability, version control and export to other systems. See `restapi` folder.
* No special treatment is given to currencies, localization, timezones and security. This is to keep things simple.
   
## Prove of Work

Entire user journey tested within this IT `org.adamd.demo.IT.RestUserJourneyIntegrationTest`. A cucumber BDD feature
file would describe this better, but not provided for simplicity.

Journey of the user involves:
1. Logging in.
2. Browsing available movies.
3. Renting some movies.
4. Paying rent charges.
5. Returning some movies.
6. Paying late returns surcharges.
7. Checking bonus points
8. Checking un-returned rentals.

# Compilation and Execution

Requirements:
* Gradle 5.6+
  * If this is not available run warpper scripts: `gradlew`
* JDK 11

Build and validate:
* `gradle clean build test --tests "org.adamd.demo.*" `

run:
* `gradle bootRun`
* Access RESTAPI using swagger on: `http://localhost:8080/swagger-ui.html`
* Webflux has disabled the automatic H2 console, but can be enabled by running manually the jetty server
and navigating to: `http://localhost:8080/h2` with username: _sa_ password: _password_. 

To build OpenApi Yamls:
* `gradle generateOpenApiDocs`
* These will be outputted to: _build/restapi-docs/rental-restopenapi.json_
