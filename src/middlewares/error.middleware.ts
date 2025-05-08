// src/middlewares/error.middleware.ts

import { Request, Response, NextFunction } from 'express';
import { AppError } from '../utils/error.util';
import axios from 'axios';

// Define a type for the potential error data structure from Keycloak
interface KeycloakErrorData {
    error?: string;
    error_description?: string;
    errorMessage?: string;
    // Add other potential error fields
}

// Type guard to check if an error is an Axios error
function isAxiosError(error: any): error is { response?: { status?: number; data?: KeycloakErrorData }; message: string; isAxiosError: boolean; config?: any; stack?: string } {
    return typeof error === 'object' && error !== null && error.isAxiosError === true;
}

// Define the structure for the error response
interface ErrorResponse {
    error_code: string; // Use HTTP status code as error code by default
    error_description: string;
    error_source: string; // Identify the source, e.g., 'OAuthAPI'
    error_stack?: any[]; // Optional: Include stack trace details if needed
}

export const errorMiddleware = (err: Error | any, req: Request, res: Response, next: NextFunction) => {
    console.error("Error caught by middleware:", err); // Log the full error

    let statusCode = 500;
    let errorCode = 'OA-500'; // Default internal server error code
    let errorDescription = 'Ocorreu um erro interno no servidor.';
    const errorSource = 'OAuthAPI'; // Source is this API
    let errorStack: any[] = [];

    // Add stack trace if available (might be verbose)
    if (err.stack) {
        // Simple split, might need more sophisticated parsing depending on desired detail
        errorStack = err.stack.split('\n').map((line: string) => line.trim());
    }

    if (err instanceof AppError) {
        statusCode = err.statusCode;
        errorCode = `OA-${statusCode}`; // Use AppError's status code
        errorDescription = err.message;
    } else if (isAxiosError(err)) {
        statusCode = err.response?.status || 500;
        errorCode = `KC-${statusCode}`; // Prefix Keycloak errors with KC
        errorDescription = 'Erro ao comunicar com o serviço Keycloak.';

        // Try to get a more specific description from Keycloak's response
        const errorData = err.response?.data;
        if (errorData) {
            if (errorData.error_description) {
                errorDescription = `Keycloak: ${errorData.error_description}`;
            } else if (errorData.errorMessage) {
                errorDescription = `Keycloak: ${errorData.errorMessage}`;
            } else if (typeof errorData === 'string') {
                errorDescription = `Keycloak: ${errorData}`;
            }
        }

        // Add Axios request/response details to stack for debugging
        errorStack.push({ axios_request_url: err.config?.url, axios_request_method: err.config?.method });
        errorStack.push({ axios_response_status: err.response?.status, axios_response_data: err.response?.data });

    } else if (err instanceof Error) {
        // Generic Error
        errorDescription = err.message;
    }

    const errorResponse: ErrorResponse = {
        error_code: errorCode,
        error_description: errorDescription,
        error_source: errorSource,
        // Only include stack in non-production environments or based on a flag
        // error_stack: process.env.NODE_ENV !== 'production' ? errorStack : undefined
        error_stack: errorStack // Including stack for now as per requirement
    };

    return res.status(statusCode).json(errorResponse);
};

