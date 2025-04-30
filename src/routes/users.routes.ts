import { Router, Request, Response, NextFunction, RequestHandler } from 'express'; // Importar RequestHandler
import {
  createUser,
  listUsers,
  getUser,
  updateUser,
  updateUserPassword,
  deleteUser
} from '../controllers/users.controller';

export const usersRouter = Router();

// Definir handlers com tipo explícito RequestHandler
const createUserHandler: RequestHandler = (req, res, next) => createUser(req, res, next);
const listUsersHandler: RequestHandler = (req, res, next) => listUsers(req, res, next);
const getUserHandler: RequestHandler = (req, res, next) => getUser(req, res, next);
const updateUserHandler: RequestHandler = (req, res, next) => updateUser(req, res, next);
const updateUserPasswordHandler: RequestHandler = (req, res, next) => updateUserPassword(req, res, next);
const deleteUserHandler: RequestHandler = (req, res, next) => deleteUser(req, res, next);

// Usar os handlers tipados
usersRouter.post('/', createUserHandler);
usersRouter.get('/', listUsersHandler);
usersRouter.get('/:id', getUserHandler);
usersRouter.put('/:id', updateUserHandler);
usersRouter.patch('/:id', updateUserPasswordHandler);
usersRouter.delete('/:id', deleteUserHandler);
