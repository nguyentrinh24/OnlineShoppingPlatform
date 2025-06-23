package com.project.shopapp.responses.User;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class LoginResponse {
    @JsonProperty("message")
    private String message;

    @JsonProperty("token")
    private String token;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("username")
    private String username;

    @JsonProperty("roles")
    private List<String> roles;

    @JsonProperty("id")
    private Long id;
}
