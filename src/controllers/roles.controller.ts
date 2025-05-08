// src/controllers/roles.controller.ts

import axios from 'axios';
import { Request, Response, NextFunction } from 'express';
import { KEYCLOAK_BASE_URL, REALM } from '../config/keycloak';

interface KeycloakRoleRepresentation {
  id: string;
  name: string;
  description?: string;
}

const getAuthHeaders = (token: string): any => ({
  headers: {
    Authorization: `Bearer ${token}`,
    'Content-Type': 'application/json'
  }
});

/**
 * Create a new realm role
 * POST /roles
 */
export const createRole = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const token = req.headers.authorization?.split(' ')[1]!;
    const { name, description } = req.body;
    await axios.post(
      `${KEYCLOAK_BASE_URL}/admin/realms/${REALM}/roles`,
      { name, description },
      getAuthHeaders(token)
    );
    res.status(201).json({ name, description });
  } catch (error) {
    next(error);
  }
};

/**
 * List all realm roles
 * GET /roles
 */
export const listRoles = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const token = req.headers.authorization?.split(' ')[1]!;
    const response = await axios.get<KeycloakRoleRepresentation[]>(
      `${KEYCLOAK_BASE_URL}/admin/realms/${REALM}/roles`,
      getAuthHeaders(token)
    );
    res.status(200).json(response.data);
  } catch (error) {
    next(error);
  }
};

/**
 * Get a specific realm role by name
 * GET /roles/:roleName
 */
export const getRole = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const token = req.headers.authorization?.split(' ')[1]!;
    const { roleName } = req.params;
    const response = await axios.get<KeycloakRoleRepresentation>(
      `${KEYCLOAK_BASE_URL}/admin/realms/${REALM}/roles/${roleName}`,
      getAuthHeaders(token)
    );
    res.status(200).json(response.data);
  } catch (error) {
    next(error);
  }
};

/**
 * Update a realm role fully
 * PUT /roles/:roleName
 */
export const updateRole = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const token = req.headers.authorization?.split(' ')[1]!;
    const { roleName } = req.params;
    const data = req.body;
    await axios.put(
      `${KEYCLOAK_BASE_URL}/admin/realms/${REALM}/roles/${roleName}`,
      data,
      getAuthHeaders(token)
    );
    res.status(200).send();
  } catch (error) {
    next(error);
  }
};

/**
 * Partially update a realm role (e.g., change description)
 * PATCH /roles/:roleName
 */
export const patchRole = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const token = req.headers.authorization?.split(' ')[1]!;
    const { roleName } = req.params;
    const updates = req.body as Partial<KeycloakRoleRepresentation>;

    // Fetch existing role
    const { data: existing } = await axios.get<KeycloakRoleRepresentation>(
      `${KEYCLOAK_BASE_URL}/admin/realms/${REALM}/roles/${roleName}`,
      getAuthHeaders(token)
    );

    // Build payload com a descrição atualizada
    const payload: KeycloakRoleRepresentation = {
      id: existing.id,
      name: existing.name,
      description: updates.description ?? existing.description
    };

    await axios.put(
      `${KEYCLOAK_BASE_URL}/admin/realms/${REALM}/roles/${roleName}`,
      payload,
      getAuthHeaders(token)
    );

    res.status(200).send();
  } catch (error) {
    next(error);
  }
};

/**
 * Delete (remove) a realm role
 * DELETE /roles/:roleName
 */
export const deleteRole = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const token = req.headers.authorization?.split(' ')[1]!;
    const { roleName } = req.params;
    await axios.delete(
      `${KEYCLOAK_BASE_URL}/admin/realms/${REALM}/roles/${roleName}`,
      getAuthHeaders(token)
    );
    res.status(204).send();
  } catch (error) {
    next(error);
  }
};

/**
 * Assign a realm role to a user
 * POST /roles/:roleName/users/:userId
 */
export const assignRoleToUser = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const token = req.headers.authorization?.split(' ')[1]!;
    const { roleName, userId } = req.params;

    // Fetch role representation
    const { data: role } = await axios.get<KeycloakRoleRepresentation>(
      `${KEYCLOAK_BASE_URL}/admin/realms/${REALM}/roles/${roleName}`,
      getAuthHeaders(token)
    );

    const roleRep = { id: role.id, name: role.name };

    await axios.post(
      `${KEYCLOAK_BASE_URL}/admin/realms/${REALM}/users/${userId}/role-mappings/realm`,
      [roleRep],
      getAuthHeaders(token)
    );

    res.status(204).send();
  } catch (error) {
    next(error);
  }
};

/**
 * Remove a realm role from a user
 * DELETE /roles/:roleName/users/:userId
 */
export const removeRoleFromUser = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const token = req.headers.authorization?.split(' ')[1]!;
    const { roleName, userId } = req.params;

    // Fetch role representation
    const { data: role } = await axios.get<KeycloakRoleRepresentation>(
      `${KEYCLOAK_BASE_URL}/admin/realms/${REALM}/roles/${roleName}`,
      getAuthHeaders(token)
    );

    const roleRep = { id: role.id, name: role.name };

    // axios.delete aceita um objeto com campo `data` para o body
    await axios.delete(
      `${KEYCLOAK_BASE_URL}/admin/realms/${REALM}/users/${userId}/role-mappings/realm`,
      {
        ...getAuthHeaders(token),
        data: [roleRep]
      }
    );

    res.status(204).send();
  } catch (error) {
    next(error);
  }
};
