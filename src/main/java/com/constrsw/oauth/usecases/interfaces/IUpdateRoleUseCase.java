package com.constrsw.oauth.usecases.interfaces;

import com.constrsw.oauth.model.RoleRequest;

public interface IUpdateRoleUseCase {
    void execute(String roleId, RoleRequest roleRequest);
}