// src/routes/auth.routes.ts

import { Router } from 'express';
import { login, refreshToken } from '../controllers/auth.controller';
import multer from 'multer';

const authRouter = Router();
const upload = multer();

/**
 * @openapi
 * /login:
 *   post:
 *     summary: Authenticate user and obtain tokens
 *     requestBody:
 *       content:
 *         multipart/form-data:
 *           schema:
 *             type: object
 *             required:
 *               - client_id
 *               - username
 *               - password
 *               - grant_type
 *             properties:
 *               client_id:
 *                 type: string
 *               username:
 *                 type: string
 *               password:
 *                 type: string
 *               grant_type:
 *                 type: string
 *                 enum: [password]
 *     responses:
 *       200:
 *         description: Token response
 *       400:
 *         description: Bad request (missing fields or invalid grant_type)
 *       401:
 *         description: Invalid credentials
 */
authRouter.post('/login', upload.none(), login);

/**
 * @openapi
 * /refresh:
 *   post:
 *     summary: Refresh access token
 *     requestBody:
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             required:
 *               - client_id
 *               - refresh_token
 *             properties:
 *               client_id:
 *                 type: string
 *               refresh_token:
 *                 type: string
 *     responses:
 *       200:
 *         description: New token response
 *       400:
 *         description: Invalid or expired refresh token
 */
authRouter.post('/refresh', refreshToken);

export { authRouter };
