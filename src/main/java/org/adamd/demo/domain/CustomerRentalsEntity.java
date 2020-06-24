package org.adamd.demo.domain;

import java.time.LocalDate;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "customer_rentals")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerRentalsEntity {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private BigDecimal rentalCharge;

    @NotNull
    @PastOrPresent
    @Column(columnDefinition = "DATE")
    private LocalDate dateRented;

    @FutureOrPresent
    @Column(columnDefinition = "DATE")
    private LocalDate datePromised;

    @PastOrPresent
    @Column(columnDefinition = "DATE")
    private LocalDate dateReturned;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "movie")
    private MovieEntity movie;

    @NotNull
    @OneToOne
    @JoinColumn(name = "customer")
    private CustomerEntity customer;
}
