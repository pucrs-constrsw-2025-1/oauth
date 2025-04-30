package com.constrsw.oauth.usecases.interfaces;

import com.constrsw.oauth.model.UserResponse;

import java.util.List;

public interface IGetAllUsersUseCase {
    public List<UserResponse> execute(Boolean enabled);
}
