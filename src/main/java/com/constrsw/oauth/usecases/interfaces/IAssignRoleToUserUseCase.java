package com.constrsw.oauth.usecases.interfaces;

public interface IAssignRoleToUserUseCase {
    void execute(String userId, String roleId);
}