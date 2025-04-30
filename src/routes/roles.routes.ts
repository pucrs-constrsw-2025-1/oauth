// src/routes/roles.routes.ts

import { Router } from 'express';
import {
  createRole,
  listRoles,
  getRole,
  updateRole,
  patchRole,
  deleteRole,
  assignRoleToUser,
  removeRoleFromUser
} from '../controllers/roles.controller';

export const rolesRouter = Router();

/**
 * @openapi
 * /roles:
 *   post:
 *     summary: Create a new realm role
 *     security:
 *       - bearerAuth: []
 *     requestBody:
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             required: [name]
 *             properties:
 *               name:
 *                 type: string
 *               description:
 *                 type: string
 *     responses:
 *       201:
 *         description: Role created
 */
rolesRouter.post('/', createRole);

/**
 * @openapi
 * /roles:
 *   get:
 *     summary: List all realm roles
 *     security:
 *       - bearerAuth: []
 *     responses:
 *       200:
 *         description: Array of roles
 */
rolesRouter.get('/', listRoles);

/**
 * @openapi
 * /roles/{roleName}:
 *   get:
 *     summary: Get a realm role by name
 *     security:
 *       - bearerAuth: []
 *     parameters:
 *       - in: path
 *         name: roleName
 *         required: true
 *         schema:
 *           type: string
 *     responses:
 *       200:
 *         description: Role object
 */
rolesRouter.get('/:roleName', getRole);

/**
 * @openapi
 * /roles/{roleName}:
 *   put:
 *     summary: Fully update a realm role
 *     security:
 *       - bearerAuth: []
 *     parameters:
 *       - in: path
 *         name: roleName
 *         required: true
 *         schema:
 *           type: string
 *     requestBody:
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             properties:
 *               name:
 *                 type: string
 *               description:
 *                 type: string
 *     responses:
 *       200:
 *         description: Role updated
 */
rolesRouter.put('/:roleName', updateRole);

/**
 * @openapi
 * /roles/{roleName}:
 *   patch:
 *     summary: Partially update a realm role
 *     security:
 *       - bearerAuth: []
 *     parameters:
 *       - in: path
 *         name: roleName
 *         required: true
 *         schema:
 *           type: string
 *     requestBody:
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             properties:
 *               description:
 *                 type: string
 *     responses:
 *       200:
 *         description: Role patched
 */
rolesRouter.patch('/:roleName', patchRole);

/**
 * @openapi
 * /roles/{roleName}:
 *   delete:
 *     summary: Delete (remove) a realm role
 *     security:
 *       - bearerAuth: []
 *     parameters:
 *       - in: path
 *         name: roleName
 *         required: true
 *         schema:
 *           type: string
 *     responses:
 *       204:
 *         description: Role removed
 */
rolesRouter.delete('/:roleName', deleteRole);

/**
 * @openapi
 * /roles/{roleName}/users/{userId}:
 *   post:
 *     summary: Assign a realm role to a user
 *     security:
 *       - bearerAuth: []
 *     parameters:
 *       - in: path
 *         name: roleName
 *         required: true
 *         schema:
 *           type: string
 *       - in: path
 *         name: userId
 *         required: true
 *         schema:
 *           type: string
 *     responses:
 *       204:
 *         description: Role assigned
 */
rolesRouter.post('/:roleName/users/:userId', assignRoleToUser);

/**
 * @openapi
 * /roles/{roleName}/users/{userId}:
 *   delete:
 *     summary: Remove a realm role from a user
 *     security:
 *       - bearerAuth: []
 *     parameters:
 *       - in: path
 *         name: roleName
 *         required: true
 *         schema:
 *           type: string
 *       - in: path
 *         name: userId
 *         required: true
 *         schema:
 *           type: string
 *     responses:
 *       204:
 *         description: Role removed
 */
rolesRouter.delete('/:roleName/users/:userId', removeRoleFromUser);
