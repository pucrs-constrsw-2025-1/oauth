package com.constrsw.oauth.usecases.interfaces;

public interface IRemoveRoleFromUserUseCase {
    void execute(String userId, String roleId);
}