package com.mikep.ReactApp.Exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsernameExistsResponse {
    private String username;
    public UsernameExistsResponse(String username) {
        this.username = username;
    }
}
