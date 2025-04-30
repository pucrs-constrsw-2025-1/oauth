package com.constrsw.oauth.usecases.interfaces;

import com.constrsw.oauth.model.UserRequest;

public interface ICreateUserUseCase {
    String execute (UserRequest user, boolean isTemporary);
}
