package com.constrsw.oauth.usecases.interfaces;

import java.util.Map;

public interface IPatchRoleUseCase {
    void execute(String roleId, Map<String, Object> updates);
}