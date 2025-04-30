import axios from 'axios';
import { Request, Response, NextFunction } from 'express';
import { KEYCLOAK_BASE_URL, REALM } from '../config/keycloak';

const getAuthHeaders = (token: string) => ({
  headers: {
    Authorization: `Bearer ${token}`
  }
});

export const validateAccess = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const token = req.headers.authorization?.split(' ')[1];
    const { resource } = req.query;

    if (!resource) {
        res.status(400).json({ message: "Resource não informado." });
        return; // Adicionar um return vazio
      }
      

    const userInfo = await axios.get<any>(
        `${KEYCLOAK_BASE_URL}/realms/${REALM}/protocol/openid-connect/userinfo`,
        getAuthHeaders(token!)
      );
      
      const roles: string[] = userInfo.data.realm_access?.roles || [];
      

    const permissionsMapping: Record<string, string[]> = {
      administrator: ['resources', 'rooms', 'professors', 'students'],
      coordinator: ['courses', 'classes'],
      professor: ['lessons', 'reservations']
    };

    const authorized = roles.some((role: string) =>
        permissionsMapping[role]?.includes(resource as string)
      );
      

    if (authorized) {
      res.status(200).json({ message: "Acesso permitido." });
    } else {
      res.status(403).json({ message: "Acesso negado." });
    }
  } catch (error) {
    next(error);
  }
};
