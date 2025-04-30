package com.constrsw.oauth.usecases.interfaces;

import com.constrsw.oauth.model.UserRequest;

public interface IUpdateUserUseCase {
    void execute(String id, UserRequest user);
}
