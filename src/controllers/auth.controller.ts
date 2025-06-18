import axios from 'axios';
import type { Request, Response, NextFunction, RequestHandler } from 'express';
import { KEYCLOAK_BASE_URL, REALM, CLIENT_ID, CLIENT_SECRET } from '../config/keycloak';

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

// Type guard to check if an error is an Axios error
function isAxiosError(error: any): error is { response?: { status?: number; data?: any }; message: string; isAxiosError: boolean; config?: any } {
  return typeof error === 'object' && error !== null && (error as any).isAxiosError === true;
}

// POST /login
export const login: RequestHandler = async (req: Request, res: Response, next: NextFunction) => {
  // Log incoming request details for debugging
  console.log('Login Request Headers:', req.headers);
  console.log('Login Request Origin:', req.headers.origin);
  console.log('Login Request Body:', req.body);

  // Set CORS headers explicitly
  res.header('Access-Control-Allow-Origin', req.headers.origin || '*');
  res.header('Access-Control-Allow-Credentials', 'true');
  res.header('Access-Control-Allow-Methods', 'GET,HEAD,PUT,PATCH,POST,DELETE');
  res.header('Access-Control-Allow-Headers', 'Origin, X-Requested-With, Content-Type, Accept, Authorization');

  try {
    const { username, password } = req.body;

    // Validate input
    if (!username || !password) {
      return res.status(400).json({
        error_code: '400',
        error_description: 'username e password são obrigatórios.',
        error_source: 'OAuthAPI',
        error_stack: []
      });
    }

    const params = new URLSearchParams();
    params.append('client_id', process.env.KEYCLOAK_CLIENT_ID || '');
    params.append('client_secret', process.env.KEYCLOAK_CLIENT_SECRET || '');
    params.append('grant_type', 'password');
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

    // 201 Created
    return res.status(201).json(responseData);
  } catch (error) {
    if (isAxiosError(error)) {
      // Invalid credentials
      if (error.response?.status === 401) {
        return res.status(401).json({
          error_code: '401',
          error_description: 'username e/ou password inválidos.',
          error_source: 'OAuthAPI',
          error_stack: [error.message]
        });
      }
      // Other errors from Keycloak
      const status = error.response?.status || 500;
      return res.status(status).json({
        error_code: status.toString(),
        error_description: error.response?.data?.error_description || 'Erro ao tentar fazer login.',
        error_source: 'OAuthAPI',
        error_stack: [error.message]
      });
    }
    next(error);
  }
};

// POST /refresh
export const refreshToken: RequestHandler = async (req: Request, res: Response, next: NextFunction) => {
  // Log incoming request details for debugging
  console.log('Refresh Token Request Headers:', req.headers);
  console.log('Refresh Token Request Origin:', req.headers.origin);
  console.log('Refresh Token Request Body:', req.body);

  // Set CORS headers explicitly
  res.header('Access-Control-Allow-Origin', req.headers.origin || '*');
  res.header('Access-Control-Allow-Credentials', 'true');
  res.header('Access-Control-Allow-Methods', 'GET,HEAD,PUT,PATCH,POST,DELETE');
  res.header('Access-Control-Allow-Headers', 'Origin, X-Requested-With, Content-Type, Accept, Authorization');

  try {
    const { refresh_token } = req.body;

    // Validate input
    if (!refresh_token) {
      return res.status(400).json({
        error_code: '400',
        error_description: 'refresh_token é obrigatório.',
        error_source: 'OAuthAPI',
        error_stack: []
      });
    }

    const params = new URLSearchParams();
    params.append('client_id', process.env.KEYCLOAK_CLIENT_ID || '');
    params.append('client_secret', process.env.KEYCLOAK_CLIENT_SECRET || '');
    params.append('grant_type', 'refresh_token');
    params.append('refresh_token', refresh_token);

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

    return res.status(200).json(responseData);
  } catch (error) {
    if (isAxiosError(error)) {
      // Invalid or expired refresh token
      if (error.response?.status === 400) {
        return res.status(400).json({
          error_code: '400',
          error_description: 'refresh token inválido ou expirado.',
          error_source: 'OAuthAPI',
          error_stack: [error.message]
        });
      }
      const status = error.response?.status || 500;
      return res.status(status).json({
        error_code: status.toString(),
        error_description: 'Erro ao tentar renovar o token.',
        error_source: 'OAuthAPI',
        error_stack: [error.message]
      });
    }
    next(error);
  }
};
