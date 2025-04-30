//error.middleware.ts


import { Request, Response, NextFunction } from 'express';
import { AppError } from '../utils/error.util';
import axios from 'axios'; // Keep axios import

// Define a type for the potential error data structure from Keycloak
interface KeycloakErrorData {
    error?: string;
    error_description?: string;
    errorMessage?: string;
    // Add other potential error fields
}

// Type guard to check if an error is an Axios error by checking properties
function isAxiosError(error: any): error is { response?: { status?: number; data?: KeycloakErrorData }; message: string; isAxiosError: boolean; config?: any } {
    return typeof error === 'object' && error !== null && error.isAxiosError === true;
}

export const errorMiddleware = (err: Error, req: Request, res: Response, next: NextFunction) => {
  console.error("Error caught by middleware:", err); // Log the full error

  if (err instanceof AppError) {
    return res.status(err.statusCode).json({ message: err.message });
  }

  // Use the custom type guard
  if (isAxiosError(err)) {
    // Log detailed Axios error information
    console.error("Axios Error Details:", {
        message: err.message,
        url: err.config?.url,
        method: err.config?.method,
        status: err.response?.status,
        data: err.response?.data
    });

    // Provide a more specific message based on Keycloak common errors if possible
    const status = err.response?.status || 500;
    let message = 'Erro ao comunicar com o serviço de autenticação.';

    if (status === 401) {
        message = 'Erro de autenticação ou autorização com o Keycloak. Verifique o token ou credenciais.';
    } else if (status === 403) {
        message = 'Permissões insuficientes no Keycloak para realizar a operação.';
    } else if (status === 404) {
        message = 'Recurso não encontrado no Keycloak.';
    } else if (status === 409) {
        message = 'Conflito ao tentar criar ou modificar recurso no Keycloak (ex: usuário já existe).';
    // Check if response data exists and has the expected properties
    } else if (err.response?.data) {
        const errorData = err.response.data;
        if (errorData.error_description) {
            message = errorData.error_description;
        } else if (errorData.errorMessage) {
            message = errorData.errorMessage;
        }
    }

    return res.status(status).json({ message: message, details: err.response?.data });
  }

  // Generic fallback for other types of errors
  return res.status(500).json({ message: 'Ocorreu um erro interno no servidor.', error: err.message });
};

