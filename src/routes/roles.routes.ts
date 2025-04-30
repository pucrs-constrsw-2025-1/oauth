import { Router } from 'express';
import {
  createRole,
  listRoles,
  getRole,
  updateRole,
  deleteRole
} from '../controllers/roles.controller';

export const rolesRouter = Router();

rolesRouter.post('/', createRole);
rolesRouter.get('/', listRoles);
rolesRouter.get('/:roleName', getRole);
rolesRouter.put('/:roleName', updateRole);
rolesRouter.delete('/:roleName', deleteRole);
