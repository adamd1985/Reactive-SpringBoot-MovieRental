package org.adamd.demo.domain;

import java.time.LocalDate;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Builder
@Getter
public class RentalCharge {

    @NotNull
    @PastOrPresent
    private LocalDate timestamp;

    @NotNull
    @Size(min = 1)
    private List<CustomerRentalsEntity> rentals;


    @NotNull
    @DecimalMin("0.01")
    private BigDecimal totalCharges;

    @NotNull
    @Positive
    private BigDecimal lateCharges;

}
