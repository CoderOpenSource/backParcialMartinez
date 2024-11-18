package com.example.examen2.auth;

import java.io.Serializable;

public class JwtResponse implements Serializable {

    private static final long serialVersionUID = -8091879091924046844L;
    private final String jwttoken;
    private final String rol;
    private final Long id;

    public JwtResponse(String jwttoken, String rol, Long id) {
        this.jwttoken = jwttoken;
        this.rol = rol;
        this.id = id;
    }

    public String getToken() {
        return this.jwttoken;
    }

    public String getRol() {
        return this.rol;
    }

    public Long getId() {
        return this.id;
    }
}
