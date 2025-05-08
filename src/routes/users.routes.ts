// src/routes/users.routes.ts

import { Router, RequestHandler } from 'express';
import {
  createUser,
  listUsers,
  getUser,
  updateUser,
  updateUserPassword,
  deleteUser
} from '../controllers/users.controller';

export const usersRouter = Router();

const createUserHandler: RequestHandler = createUser;
const listUsersHandler: RequestHandler = listUsers;
const getUserHandler: RequestHandler = getUser;
const updateUserHandler: RequestHandler = updateUser;
const updateUserPasswordHandler: RequestHandler = updateUserPassword;
const deleteUserHandler: RequestHandler = deleteUser;

/**
 * @openapi
 * /users:
 *   post:
 *     summary: Create a new user
 *     security:
 *       - bearerAuth: []
 *     requestBody:
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             required: [username, password, first-name, last-name, email]
 *             properties:
 *               username:
 *                 type: string
 *               password:
 *                 type: string
 *               first-name:
 *                 type: string
 *               last-name:
 *                 type: string
 *               email:
 *                 type: string
 *     responses:
 *       201:
 *         description: User created
 *       400:
 *         description: Validation error or missing fields
 */
usersRouter.post('/', createUserHandler);

/**
 * @openapi
 * /users:
 *   get:
 *     summary: List all users
 *     security:
 *       - bearerAuth: []
 *     parameters:
 *       - in: query
 *         name: enabled
 *         schema:
 *           type: boolean
 *         description: Filter by enabled status
 *     responses:
 *       200:
 *         description: Array of users
 */
usersRouter.get('/', listUsersHandler);

/**
 * @openapi
 * /users/{id}:
 *   get:
 *     summary: Get a user by ID
 *     security:
 *       - bearerAuth: []
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         schema:
 *           type: string
 *     responses:
 *       200:
 *         description: User object
 *       404:
 *         description: User not found
 */
usersRouter.get('/:id', getUserHandler);

/**
 * @openapi
 * /users/{id}:
 *   put:
 *     summary: Update a user
 *     security:
 *       - bearerAuth: []
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         schema:
 *           type: string
 *     requestBody:
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             properties:
 *               first-name:
 *                 type: string
 *               last-name:
 *                 type: string
 *               email:
 *                 type: string
 *     responses:
 *       200:
 *         description: User updated
 */
usersRouter.put('/:id', updateUserHandler);

/**
 * @openapi
 * /users/{id}:
 *   patch:
 *     summary: Change user password
 *     security:
 *       - bearerAuth: []
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         schema:
 *           type: string
 *     requestBody:
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             required: [password]
 *             properties:
 *               password:
 *                 type: string
 *     responses:
 *       200:
 *         description: Password changed
 */
usersRouter.patch('/:id', updateUserPasswordHandler);

/**
 * @openapi
 * /users/{id}:
 *   delete:
 *     summary: Disable (logical delete) a user
 *     security:
 *       - bearerAuth: []
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         schema:
 *           type: string
 *     responses:
 *       204:
 *         description: User disabled
 */
usersRouter.delete('/:id', deleteUserHandler);
