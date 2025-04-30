package com.constrsw.oauth.usecases.interfaces;

import com.constrsw.oauth.model.TokenResponse;

public interface ILoginUseCase {
    TokenResponse execute(String username, String password);
}
