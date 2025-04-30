import { Router, Request, Response, NextFunction, RequestHandler } from 'express'; // Importar RequestHandler
import { validateAccess } from '../controllers/validate.controller';

export const validateRouter = Router();

// Definir handler com tipo explícito RequestHandler
const validateAccessHandler: RequestHandler = (req, res, next) => validateAccess(req, res, next);

// Usar o handler tipado
validateRouter.get('/validate-token', validateAccess); // <-- Rota correta