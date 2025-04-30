import axios from 'axios'; // Keep axios import
import { Request, Response, NextFunction } from 'express';
import { KEYCLOAK_BASE_URL, REALM, CLIENT_SECRET } from '../config/keycloak';

// Interface for the token response from Keycloak
interface KeycloakTokenResponse {
    access_token: string;
    expires_in: number;
    refresh_expires_in: number;
    refresh_token: string;
    token_type: string;
    'not-before-policy'?: number;
    session_state?: string;
    scope?: string;
}

// Interface for the response body required by the specification
interface LoginResponse {
    token_type: string;
    access_token: string;
    expires_in: number;
    refresh_token: string;
    refresh_expires_in: number;
}

// Type guard to check if an error is an Axios error by checking properties
function isAxiosError(error: any): error is { response?: { status?: number; data?: any }; message: string; isAxiosError: boolean; config?: any } {
    return typeof error === 'object' && error !== null && error.isAxiosError === true;
}

export const login = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const { client_id, username, password, grant_type } = req.body;

    if (!client_id || !username || !password || grant_type !== 'password') {
      return res.status(400).json({ message: 'Parâmetros inválidos: client_id, username, password e grant_type=password são obrigatórios.' });
    }

    const params = new URLSearchParams();
    params.append('client_id', client_id);
    params.append('client_secret', CLIENT_SECRET);
    params.append('grant_type', grant_type);
    params.append('username', username);
    params.append('password', password);

    const response = await axios.post<KeycloakTokenResponse>(
      `${KEYCLOAK_BASE_URL}/realms/${REALM}/protocol/openid-connect/token`,
      params,
      { headers: { 'Content-Type': 'application/x-www-form-urlencoded' } }
    );

    const responseData: LoginResponse = {
        token_type: response.data.token_type,
        access_token: response.data.access_token,
        expires_in: response.data.expires_in,
        refresh_token: response.data.refresh_token,
        refresh_expires_in: response.data.refresh_expires_in
    };

    res.status(200).json(responseData);
  } catch (error) {
    // Use the custom type guard
    if (isAxiosError(error)) {
      console.error("Axios error during login:", error.response?.data || error.message);
      if (error.response?.status === 401) {
        return res.status(401).json({ message: 'Credenciais inválidas.' });
      }
      return res.status(error.response?.status || 500).json(error.response?.data || { message: 'Erro ao tentar fazer login.' });
    } else {
      console.error("Unexpected error during login:", error);
      next(error);
    }
  }
};

export const refreshToken = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const { client_id, refresh_token } = req.body;

    if (!client_id || !refresh_token) {
      return res.status(400).json({ message: "client_id e refresh_token são obrigatórios." });
    }

    const params = new URLSearchParams();
    params.append("client_id", client_id);
    params.append("client_secret", CLIENT_SECRET);
    params.append("grant_type", "refresh_token");
    params.append("refresh_token", refresh_token);

    const response = await axios.post<KeycloakTokenResponse>(
      `${KEYCLOAK_BASE_URL}/realms/${REALM}/protocol/openid-connect/token`,
      params,
      { headers: { 'Content-Type': 'application/x-www-form-urlencoded' } }
    );

    const responseData: LoginResponse = {
        token_type: response.data.token_type,
        access_token: response.data.access_token,
        expires_in: response.data.expires_in,
        refresh_token: response.data.refresh_token,
        refresh_expires_in: response.data.refresh_expires_in
    };

    res.status(200).json(responseData);
  } catch (error) {
    // Use the custom type guard
    if (isAxiosError(error)) {
      if (error.response?.status === 400) {
        console.error("Refresh token inválido ou expirado:", error.response?.data);
        return res.status(400).json({ message: "Refresh token inválido ou expirado." });
      }
      console.error("Axios error during refresh token:", error.response?.data || error.message);
      return res.status(error.response?.status || 500).json(error.response?.data || { message: 'Erro ao tentar renovar o token.' });
    } else {
      console.error("An unexpected error occurred during refresh token:", error);
      next(error);
    }
  }
};

