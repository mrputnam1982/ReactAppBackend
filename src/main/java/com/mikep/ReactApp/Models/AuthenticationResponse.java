package com.mikep.ReactApp.Models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse implements Serializable {
    private static final long serialVersionUID = 8317676219297719109L;
    private String jwt;
    private String username;
    private Set<Role> roles;
    private Image avatar;
    public String getToken() {
        return jwt;
    }
}
