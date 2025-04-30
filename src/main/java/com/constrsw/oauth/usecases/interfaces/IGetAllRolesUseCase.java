package com.constrsw.oauth.usecases.interfaces;

import com.constrsw.oauth.model.RoleResponse;
import java.util.List;

public interface IGetAllRolesUseCase {
    List<RoleResponse> execute();
}