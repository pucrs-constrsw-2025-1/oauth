package com.constrsw.oauth.usecases.interfaces;

import com.constrsw.oauth.model.RoleRequest;

public interface ICreateRoleUseCase {
    void execute(RoleRequest roleRequest);
}