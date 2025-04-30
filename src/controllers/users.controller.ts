// src/controllers/users.controller.ts

import axios from 'axios';
import { Request, Response, NextFunction } from 'express';
import { KEYCLOAK_BASE_URL, REALM } from '../config/keycloak';
import { AppError } from '../utils/error.util';

// RFC 5322 official regex for email validation
const EMAIL_REGEX = new RegExp(
  `^([-!#-'*+\\/-9=?A-Z^-~]+(\\.[-!#-'*+\\/-9=?A-Z^-~]+)*|` +
  `"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f!#-\\[\\]-~]|` +
  `\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*")@` +
  `([-!#-'*+\\/-9=?A-Z^-~]+(\\.[-!#-'*+\\/-9=?A-Z^-~]+)*|` +
  `\\[[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]*\\])$`
);

// Interfaces for Keycloak representations
interface KeycloakUserRepresentation {
  id?: string;
  username?: string;
  firstName?: string;
  lastName?: string;
  email?: string;
  enabled?: boolean;
  emailVerified?: boolean;
  credentials?: Array<{ type: string; value: string; temporary: boolean }>;
  requiredActions?: string[];
}

// Type guard for Axios errors
function isAxiosError(error: any): error is { response?: { status?: number; data?: any }; isAxiosError: boolean } {
  return error?.isAxiosError === true;
}

// Helper to extract Bearer token
const extractToken = (req: Request): string => {
  const header = req.headers.authorization;
  if (!header || !header.startsWith('Bearer ')) {
    throw new AppError('Token de autorização não fornecido.', 401);
  }
  return header.split(' ')[1];
};

// Helper to build auth headers
const getAuthHeaders = (token: string) => ({
  headers: {
    Authorization: `Bearer ${token}`,
    'Content-Type': 'application/json'
  }
});

// Map Keycloak user to API spec response keys
const mapToUserResponse = (user: KeycloakUserRepresentation) => {
  if (!user.id || !user.username || !user.firstName || !user.lastName || !user.email || user.enabled === undefined) {
    console.error('Dados incompletos do Keycloak:', user);
    throw new AppError('Dados do usuário incompletos recebidos do Keycloak.', 500);
  }
  return {
    id: user.id,
    username: user.username,
    'first-name': user.firstName,
    'last-name': user.lastName,
    email: user.email,
    enabled: user.enabled
  };
};

export const createUser = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const token = extractToken(req);
    const { username, password } = req.body;
    const firstName = req.body['first-name'];
    const lastName  = req.body['last-name'];
    const email     = req.body.email;

    if (!username || !password || !firstName || !lastName || !email) {
      return res.status(400).json({
        error_code: '400',
        error_description: 'Campos obrigatórios ausentes: username, password, first-name, last-name, email.',
        error_source: 'OAuthAPI',
        error_stack: []
      });
    }

    if (!EMAIL_REGEX.test(email)) {
      return res.status(400).json({
        error_code: '400',
        error_description: 'E-mail inválido.',
        error_source: 'OAuthAPI',
        error_stack: []
      });
    }

    const kcUser: KeycloakUserRepresentation = {
      username,
      email,
      firstName,
      lastName,
      enabled: true,
      emailVerified: true,
      credentials: [{ type: 'password', value: password, temporary: false }]
    };

    const kcResponse = await axios.post(
      `${KEYCLOAK_BASE_URL}/admin/realms/${REALM}/users`,
      kcUser,
      getAuthHeaders(token)
    );

    if (kcResponse.status !== 201) {
      throw new AppError(`Keycloak retornou status inesperado ${kcResponse.status}`, kcResponse.status);
    }

    const location = kcResponse.headers.location;
    const userId = location?.split('/').pop();
    if (!userId) {
      throw new AppError('Não foi possível extrair o ID do usuário criado.', 500);
    }

    const { data: created } = await axios.get<KeycloakUserRepresentation>(
      `${KEYCLOAK_BASE_URL}/admin/realms/${REALM}/users/${userId}`,
      getAuthHeaders(token)
    );

    res.status(201).json(mapToUserResponse(created));
  } catch (error: any) {
    if (isAxiosError(error)) {
      const status = error.response?.status || 500;
      let description = 'Erro ao criar usuário no Keycloak.';
      if (status === 409)       description = 'Usuário já existe.';
      else if (status === 401)  description = 'Token inválido ou expirado.';
      else if (status === 403)  description = 'Permissões insuficientes.';
      return res.status(status).json({
        error_code: String(status),
        error_description: description,
        error_source: 'OAuthAPI',
        error_stack: []
      });
    }
    next(error);
  }
};

export const listUsers = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const token   = extractToken(req);
    const enabled = req.query.enabled as string | undefined;
    const params  = new URLSearchParams();
    if (enabled !== undefined) params.append('enabled', enabled === 'true' ? 'true' : 'false');

    const { data } = await axios.get<KeycloakUserRepresentation[]>(
      `${KEYCLOAK_BASE_URL}/admin/realms/${REALM}/users?${params}`,
      getAuthHeaders(token)
    );

    const users = data.map(mapToUserResponse);
    res.status(200).json(users);
  } catch (error: any) {
    if (isAxiosError(error)) {
      const status = error.response?.status || 500;
      let description = 'Erro ao listar usuários no Keycloak.';
      if (status === 401) description = 'Token inválido ou expirado.';
      else if (status === 403) description = 'Permissões insuficientes.';
      return res.status(status).json({
        error_code: String(status),
        error_description: description,
        error_source: 'OAuthAPI',
        error_stack: []
      });
    }
    next(error);
  }
};

export const getUser = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const token = extractToken(req);
    const { id } = req.params;

    const { data } = await axios.get<KeycloakUserRepresentation>(
      `${KEYCLOAK_BASE_URL}/admin/realms/${REALM}/users/${id}`,
      getAuthHeaders(token)
    );

    res.status(200).json(mapToUserResponse(data));
  } catch (error: any) {
    if (isAxiosError(error)) {
      const status = error.response?.status || 500;
      let description = 'Erro ao obter usuário no Keycloak.';
      if (status === 404) description = 'Usuário não encontrado.';
      else if (status === 401) description = 'Token inválido ou expirado.';
      else if (status === 403) description = 'Permissões insuficientes.';
      return res.status(status).json({
        error_code: String(status),
        error_description: description,
        error_source: 'OAuthAPI',
        error_stack: []
      });
    }
    next(error);
  }
};

export const updateUser = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const token = extractToken(req);
    const { id } = req.params;
    const firstName = req.body['first-name'];
    const lastName  = req.body['last-name'];
    const email     = req.body.email;

    if (!firstName && !lastName && !email) {
      return res.status(400).json({
        error_code: '400',
        error_description: 'Nenhum dado para atualização.',
        error_source: 'OAuthAPI',
        error_stack: []
      });
    }

    if (email && !EMAIL_REGEX.test(email)) {
      return res.status(400).json({
        error_code: '400',
        error_description: 'E-mail inválido.',
        error_source: 'OAuthAPI',
        error_stack: []
      });
    }

    const { data: current } = await axios.get<KeycloakUserRepresentation>(
      `${KEYCLOAK_BASE_URL}/admin/realms/${REALM}/users/${id}`,
      getAuthHeaders(token)
    );

    const updated: Partial<KeycloakUserRepresentation> = {
      firstName: firstName ?? current.firstName,
      lastName:  lastName  ?? current.lastName,
      email:     email     ?? current.email
    };

    const toSend = { ...current, ...updated };
    delete toSend.id;
    delete toSend.credentials;
    delete toSend.requiredActions;

    await axios.put(
      `${KEYCLOAK_BASE_URL}/admin/realms/${REALM}/users/${id}`,
      toSend,
      getAuthHeaders(token)
    );

    res.status(200).send();
  } catch (error: any) {
    if (isAxiosError(error)) {
      const status = error.response?.status || 500;
      let description = 'Erro ao atualizar usuário no Keycloak.';
      if (status === 404) description = 'Usuário não encontrado.';
      else if (status === 401) description = 'Token inválido ou expirado.';
      else if (status === 403) description = 'Permissões insuficientes.';
      else if (status === 409) description = 'Conflito (e.g., e-mail em uso).';
      return res.status(status).json({
        error_code: String(status),
        error_description: description,
        error_source: 'OAuthAPI',
        error_stack: []
      });
    }
    next(error);
  }
};

export const updateUserPassword = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const token = extractToken(req);
    const { id } = req.params;
    const { password } = req.body;

    if (!password) {
      return res.status(400).json({
        error_code: '400',
        error_description: 'Nova senha não fornecida.',
        error_source: 'OAuthAPI',
        error_stack: []
      });
    }

    await axios.put(
      `${KEYCLOAK_BASE_URL}/admin/realms/${REALM}/users/${id}/reset-password`,
      { type: 'password', value: password, temporary: false },
      getAuthHeaders(token)
    );

    res.status(200).send();
  } catch (error: any) {
    if (isAxiosError(error)) {
      const status = error.response?.status || 500;
      let description = 'Erro ao atualizar senha no Keycloak.';
      if (status === 404) description = 'Usuário não encontrado.';
      else if (status === 401) description = 'Token inválido ou expirado.';
      else if (status === 403) description = 'Permissões insuficientes.';
      return res.status(status).json({
        error_code: String(status),
        error_description: description,
        error_source: 'OAuthAPI',
        error_stack: []
      });
    }
    next(error);
  }
};

export const deleteUser = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const token = extractToken(req);
    const { id } = req.params;

    const { data: current } = await axios.get<KeycloakUserRepresentation>(
      `${KEYCLOAK_BASE_URL}/admin/realms/${REALM}/users/${id}`,
      getAuthHeaders(token)
    );

    if (current.enabled) {
      const disabled = { ...current, enabled: false };
      delete (disabled as any).id;
      delete (disabled as any).credentials;
      delete (disabled as any).requiredActions;

      await axios.put(
        `${KEYCLOAK_BASE_URL}/admin/realms/${REALM}/users/${id}`,
        disabled,
        getAuthHeaders(token)
      );
    }

    res.status(204).send();
  } catch (error: any) {
    if (isAxiosError(error)) {
      const status = error.response?.status || 500;
      let description = 'Erro ao desabilitar usuário no Keycloak.';
      if (status === 404) description = 'Usuário não encontrado.';
      else if (status === 401) description = 'Token inválido ou expirado.';
      else if (status === 403) description = 'Permissões insuficientes.';
      return res.status(status).json({
        error_code: String(status),
        error_description: description,
        error_source: 'OAuthAPI',
        error_stack: []
      });
    }
    next(error);
  }
};
