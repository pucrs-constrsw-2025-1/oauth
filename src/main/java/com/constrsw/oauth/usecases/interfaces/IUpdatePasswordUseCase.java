package com.constrsw.oauth.usecases.interfaces;

public interface IUpdatePasswordUseCase {
    void execute(String id, String password);
}
