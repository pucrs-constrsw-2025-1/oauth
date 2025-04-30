package com.constrsw.oauth.usecases.interfaces;

import com.constrsw.oauth.model.UserResponse;

public interface IGetUserByIdUseCase {
    UserResponse execute(String id);
}
