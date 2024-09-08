package net.hauntedstudio.Authentication.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuthResponse {
    private int responseCode;
    private String token;

    public AuthResponse() {
    }

    public AuthResponse(int responseCode, String token) {
        this.responseCode = responseCode;
        this.token = token;
    }

}
