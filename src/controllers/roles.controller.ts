import axios from 'axios';
import { Request, Response, NextFunction } from 'express';
import { KEYCLOAK_BASE_URL, REALM } from '../config/keycloak';

const getAuthHeaders = (token: string) => ({
  headers: {
    Authorization: `Bearer ${token}`
  }
});

export const createRole = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const token = req.headers.authorization?.split(' ')[1];
    const { name, description } = req.body;

    await axios.post(
      `${KEYCLOAK_BASE_URL}/admin/realms/${REALM}/roles`,
      { name, description },
      getAuthHeaders(token!)
    );

    res.status(201).json({ name, description });
  } catch (error) {
    next(error);
  }
};

export const listRoles = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const token = req.headers.authorization?.split(' ')[1];

    const response = await axios.get(
      `${KEYCLOAK_BASE_URL}/admin/realms/${REALM}/roles`,
      getAuthHeaders(token!)
    );

    res.status(200).json(response.data);
  } catch (error) {
    next(error);
  }
};

export const getRole = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const token = req.headers.authorization?.split(' ')[1];
    const { roleName } = req.params;

    const response = await axios.get(
      `${KEYCLOAK_BASE_URL}/admin/realms/${REALM}/roles/${roleName}`,
      getAuthHeaders(token!)
    );

    res.status(200).json(response.data);
  } catch (error) {
    next(error);
  }
};

export const updateRole = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const token = req.headers.authorization?.split(' ')[1];
    const { roleName } = req.params;
    const data = req.body;

    await axios.put(
      `${KEYCLOAK_BASE_URL}/admin/realms/${REALM}/roles/${roleName}`,
      data,
      getAuthHeaders(token!)
    );

    res.status(200).send();
  } catch (error) {
    next(error);
  }
};

export const deleteRole = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const token = req.headers.authorization?.split(' ')[1];
    const { roleName } = req.params;

    await axios.delete(
      `${KEYCLOAK_BASE_URL}/admin/realms/${REALM}/roles/${roleName}`,
      getAuthHeaders(token!)
    );

    res.status(204).send();
  } catch (error) {
    next(error);
  }
};
