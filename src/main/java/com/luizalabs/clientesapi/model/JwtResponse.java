package com.luizalabs.clientesapi.model;

public class JwtResponse {

    private String JwtToken;

    public JwtResponse(String JwtToken) {
        this.JwtToken = JwtToken;
    }

    public String getJwtToken(){
        return this.JwtToken;
    }
}