package com.mikep.ReactApp.Exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomInvalidKeyResponse {
    private String key;
    public CustomInvalidKeyResponse(String key) {
        this.key = key;
    }
}