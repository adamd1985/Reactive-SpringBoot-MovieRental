package org.adamd.demo.input;

import java.time.LocalDate;
import java.util.List;
import javax.persistence.Column;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.adamd.demo.domain.CustomerEntity;
import org.adamd.demo.domain.MovieEntity;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RentalRequestTO {

    @NotNull
    @PastOrPresent
    private LocalDate timestamp;

    @NotNull
    @Size(min = 1)
    private List<MovieEntity> movies;

    @NotNull
    private CustomerEntity customerEntity;

    @NotNull
    @FutureOrPresent
    private LocalDate datePromised;
}
