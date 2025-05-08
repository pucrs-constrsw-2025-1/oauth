// src/routes/validate.routes.ts

import { Router, RequestHandler } from 'express';
import { validateAccess } from '../controllers/validate.controller';

export const validateRouter = Router();
const validateAccessHandler: RequestHandler = validateAccess;

/**
 * @openapi
 * /auth/validate-token:
 *   get:
 *     summary: Validate token and resource access
 *     security:
 *       - bearerAuth: []
 *     parameters:
 *       - in: query
 *         name: resource
 *         required: true
 *         schema:
 *           type: string
 *         description: Resource to check permission for
 *     responses:
 *       200:
 *         description: Access allowed
 *       403:
 *         description: Access denied
 *       400:
 *         description: Missing resource query
 */
validateRouter.get('/validate-token', validateAccessHandler);
