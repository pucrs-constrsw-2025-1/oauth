import axios from 'axios'; // Keep axios import
import { Request, Response, NextFunction } from 'express';
import { KEYCLOAK_BASE_URL, REALM } from '../config/keycloak';
import { AppError } from '../utils/error.util';

// Define interfaces for Keycloak responses for better typing
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

interface UserResponse {
    id: string;
    username: string;
    firstName: string;
    lastName: string;
    email: string;
    enabled: boolean;
}

// Type guard to check if an error is an Axios error by checking properties
function isAxiosError(error: any): error is { response?: { status?: number; data?: any }; message: string; isAxiosError: boolean; config?: any } {
    return typeof error === 'object' && error !== null && error.isAxiosError === true;
}

const getAuthHeaders = (token: string | undefined) => {
  if (!token) {
    throw new AppError('Token de autorização não fornecido.', 401);
  }
  return {
    headers: {
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json'
    }
  };
};

const extractToken = (req: Request): string | undefined => {
  return req.headers.authorization?.split(' ')[1];
};

const mapToUserResponse = (user: KeycloakUserRepresentation): UserResponse => {
    if (!user.id || !user.username || !user.firstName || !user.lastName || !user.email || user.enabled === undefined) {
        console.error("Incomplete user data received from Keycloak:", user);
        throw new AppError('Dados do usuário incompletos recebidos do Keycloak.', 500);
    }
    return {
        id: user.id,
        username: user.username,
        firstName: user.firstName,
        lastName: user.lastName,
        email: user.email,
        enabled: user.enabled
    };
};

export const createUser = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const token = extractToken(req);
    const { username, password, firstName, lastName, email } = req.body;

    if (!username || !password || !firstName || !lastName || !email) {
        return res.status(400).json({ message: 'Campos obrigatórios ausentes: username, password, firstName, lastName, email.' });
    }

    const userRepresentation: KeycloakUserRepresentation = {
      username,
      email,
      firstName,
      lastName,
      enabled: true,
      emailVerified: true,
      credentials: [{ type: "password", value: password, temporary: false }]
    };

    const response = await axios.post(
      `${KEYCLOAK_BASE_URL}/admin/realms/${REALM}/users`,
      userRepresentation,
      getAuthHeaders(token)
    );

    if (response.status === 201) {
        const locationHeader = response.headers['location'];
        const userId = locationHeader?.split('/').pop();

        if (userId) {
            const newUserResponse = await axios.get<KeycloakUserRepresentation>(
                `${KEYCLOAK_BASE_URL}/admin/realms/${REALM}/users/${userId}`,
                getAuthHeaders(token)
            );
            res.status(201).json(mapToUserResponse(newUserResponse.data));
        } else {
             throw new AppError('Não foi possível extrair o ID do usuário criado.', 500);
        }
    } else {
        throw new AppError(`Keycloak respondeu com status inesperado ${response.status} ao criar usuário.`, response.status);
    }

  } catch (error) {
    // Use the custom type guard
    if (isAxiosError(error)) {
        console.error("Axios error creating user:", error.response?.data || error.message);
        if (error.response?.status === 409) {
            return res.status(409).json({ message: 'Usuário (username ou email) já existe.' });
        }
        if (error.response?.status === 401) {
            return res.status(401).json({ message: 'Token de acesso inválido ou expirado.' });
        }
        if (error.response?.status === 403) {
            return res.status(403).json({ message: 'Permissões insuficientes para criar usuário.' });
        }
        return res.status(error.response?.status || 500).json(error.response?.data || { message: 'Erro ao criar usuário no Keycloak.' });
    } else {
        console.error("Unexpected error creating user:", error);
        next(error);
    }
  }
};

export const listUsers = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const token = extractToken(req);
    const { enabled } = req.query;

    const params = new URLSearchParams();
    if (enabled !== undefined) {
        params.append('enabled', enabled === 'true' ? 'true' : 'false');
    }

    const response = await axios.get<KeycloakUserRepresentation[]>(
      `${KEYCLOAK_BASE_URL}/admin/realms/${REALM}/users?${params.toString()}`,
      getAuthHeaders(token)
    );

    const users = response.data
        .map(user => {
            try {
                return mapToUserResponse(user);
            } catch (mapError) {
                console.error("Error mapping user:", mapError, user);
                return null;
            }
        })
        .filter(user => user !== null) as UserResponse[];

    res.status(200).json(users);
  } catch (error) {
    // Use the custom type guard
    if (isAxiosError(error)) {
        console.error("Axios error listing users:", error.response?.data || error.message);
        if (error.response?.status === 401) {
            return res.status(401).json({ message: 'Token de acesso inválido ou expirado.' });
        }
        if (error.response?.status === 403) {
            return res.status(403).json({ message: 'Permissões insuficientes para listar usuários.' });
        }
        return res.status(error.response?.status || 500).json(error.response?.data || { message: 'Erro ao listar usuários no Keycloak.' });
    } else {
        console.error("Unexpected error listing users:", error);
        next(error);
    }
  }
};

export const getUser = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const token = extractToken(req);
    const { id } = req.params;

    const response = await axios.get<KeycloakUserRepresentation>(
      `${KEYCLOAK_BASE_URL}/admin/realms/${REALM}/users/${id}`,
      getAuthHeaders(token)
    );

    res.status(200).json(mapToUserResponse(response.data));
  } catch (error) {
    // Use the custom type guard
    if (isAxiosError(error)) {
        console.error("Axios error getting user:", error.response?.data || error.message);
        if (error.response?.status === 404) {
            return res.status(404).json({ message: 'Usuário não encontrado.' });
        }
        if (error.response?.status === 401) {
            return res.status(401).json({ message: 'Token de acesso inválido ou expirado.' });
        }
        if (error.response?.status === 403) {
            return res.status(403).json({ message: 'Permissões insuficientes para obter usuário.' });
        }
        return res.status(error.response?.status || 500).json(error.response?.data || { message: 'Erro ao obter usuário no Keycloak.' });
    } else {
        console.error("Unexpected error getting user:", error);
        next(error);
    }
  }
};

export const updateUser = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const token = extractToken(req);
    const { id } = req.params;
    const { firstName, lastName, email } = req.body;

    if (!firstName && !lastName && !email) {
        return res.status(400).json({ message: 'Nenhum dado fornecido para atualização.' });
    }

    const currentUserResponse = await axios.get<KeycloakUserRepresentation>(
        `${KEYCLOAK_BASE_URL}/admin/realms/${REALM}/users/${id}`,
        getAuthHeaders(token)
    );
    const currentUser = currentUserResponse.data;

    const updatedUser: Partial<KeycloakUserRepresentation> = {
        firstName: firstName !== undefined ? firstName : currentUser.firstName,
        lastName: lastName !== undefined ? lastName : currentUser.lastName,
        email: email !== undefined ? email : currentUser.email,
    };

    const finalUserRepresentationToSend = { ...currentUser, ...updatedUser };

    delete finalUserRepresentationToSend.id;
    delete finalUserRepresentationToSend.credentials;
    delete finalUserRepresentationToSend.requiredActions;

    await axios.put(
      `${KEYCLOAK_BASE_URL}/admin/realms/${REALM}/users/${id}`,
      finalUserRepresentationToSend,
      getAuthHeaders(token)
    );

    res.status(200).send();
  } catch (error) {
    // Use the custom type guard
    if (isAxiosError(error)) {
        console.error("Axios error updating user:", error.response?.data || error.message);
        if (error.response?.status === 404) {
            return res.status(404).json({ message: 'Usuário não encontrado.' });
        }
        if (error.response?.status === 401) {
            return res.status(401).json({ message: 'Token de acesso inválido ou expirado.' });
        }
        if (error.response?.status === 403) {
            return res.status(403).json({ message: 'Permissões insuficientes para atualizar usuário.' });
        }
        if (error.response?.status === 409) {
            return res.status(409).json({ message: 'Conflito (ex: email já em uso).' });
        }
        return res.status(error.response?.status || 500).json(error.response?.data || { message: 'Erro ao atualizar usuário no Keycloak.' });
    } else {
        console.error("Unexpected error updating user:", error);
        next(error);
    }
  }
};

export const updateUserPassword = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const token = extractToken(req);
    const { id } = req.params;
    const { password } = req.body;

    if (!password) {
      return res.status(400).json({ message: 'Nova senha não fornecida.' });
    }

    const credentialRepresentation = {
      type: "password",
      value: password,
      temporary: false
    };

    await axios.put(
      `${KEYCLOAK_BASE_URL}/admin/realms/${REALM}/users/${id}/reset-password`,
      credentialRepresentation,
      getAuthHeaders(token)
    );

    res.status(200).send();
  } catch (error) {
    // Use the custom type guard
     if (isAxiosError(error)) {
        console.error("Axios error updating password:", error.response?.data || error.message);
        if (error.response?.status === 404) {
            return res.status(404).json({ message: 'Usuário não encontrado.' });
        }
        if (error.response?.status === 401) {
            return res.status(401).json({ message: 'Token de acesso inválido ou expirado.' });
        }
        if (error.response?.status === 403) {
            return res.status(403).json({ message: 'Permissões insuficientes para atualizar senha.' });
        }
        return res.status(error.response?.status || 500).json(error.response?.data || { message: 'Erro ao atualizar senha no Keycloak.' });
    } else {
        console.error("Unexpected error updating password:", error);
        next(error);
    }
  }
};

export const deleteUser = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const token = extractToken(req);
    const { id } = req.params;

    const currentUserResponse = await axios.get<KeycloakUserRepresentation>(
        `${KEYCLOAK_BASE_URL}/admin/realms/${REALM}/users/${id}`,
        getAuthHeaders(token)
    );
    const currentUser = currentUserResponse.data;

    if (currentUser.enabled) {
        const updatedUserRepresentation = { ...currentUser, enabled: false };

        delete updatedUserRepresentation.id;
        delete updatedUserRepresentation.credentials;
        delete updatedUserRepresentation.requiredActions;

        await axios.put(
          `${KEYCLOAK_BASE_URL}/admin/realms/${REALM}/users/${id}`,
          updatedUserRepresentation,
          getAuthHeaders(token)
        );
    }

    res.status(204).send();
  } catch (error) {
    // Use the custom type guard
    if (isAxiosError(error)) {
        console.error("Axios error disabling user:", error.response?.data || error.message);
        if (error.response?.status === 404) {
            return res.status(404).json({ message: 'Usuário não encontrado.' });
        }
        if (error.response?.status === 401) {
            return res.status(401).json({ message: 'Token de acesso inválido ou expirado.' });
        }
        if (error.response?.status === 403) {
            return res.status(403).json({ message: 'Permissões insuficientes para desabilitar usuário.' });
        }
        return res.status(error.response?.status || 500).json(error.response?.data || { message: 'Erro ao desabilitar usuário no Keycloak.' });
    } else {
        console.error("Unexpected error disabling user:", error);
        next(error);
    }
  }
};

