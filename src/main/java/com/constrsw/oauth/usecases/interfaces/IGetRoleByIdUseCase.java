package com.constrsw.oauth.usecases.interfaces;

import com.constrsw.oauth.model.RoleResponse;

public interface IGetRoleByIdUseCase {
    RoleResponse execute(String roleId);
}