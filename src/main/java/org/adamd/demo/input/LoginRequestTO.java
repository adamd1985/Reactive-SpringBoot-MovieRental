package org.adamd.demo.input;

import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestTO {

    @NotEmpty
    private String username;

    @NotEmpty
    // TODO: obfuscate this.
    private String password;
}
