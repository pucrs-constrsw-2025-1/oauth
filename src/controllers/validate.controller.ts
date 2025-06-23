//validate.controller.ts


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
      return;
    }

    const authzRequest = {
      permissions: [{
        rsname: resource as string,
      }]
    };

    try {
      const formData = new URLSearchParams();
      formData.append('grant_type', 'urn:ietf:params:oauth:grant-type:uma-ticket');
      formData.append('audience', 'oauth');
      formData.append('permissions', JSON.stringify(authzRequest.permissions));

      await axios.post(
        `${KEYCLOAK_BASE_URL}/realms/${REALM}/protocol/openid-connect/token`,
        formData,
        {
          headers: {
            ...getAuthHeaders(token!).headers,
            'Content-Type': 'application/x-www-form-urlencoded'
          }
        }
      );
      
      res.status(200).json({ message: "Acesso permitido." });
    } catch (error: any) {
      if (error.response?.status === 403) {
        res.status(403).json({ message: "Acesso negado." });
      } else {
        throw error;
      }
    }
  } catch (error) {
    next(error);
  }
}
