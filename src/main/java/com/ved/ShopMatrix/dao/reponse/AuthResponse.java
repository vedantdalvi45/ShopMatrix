package com.ved.ShopMatrix.dao.reponse;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor // Creates a constructor with the token
public class AuthResponse {
    private String token;
}
