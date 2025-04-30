import { Router } from 'express';
import { login, refreshToken } from '../controllers/auth.controller';
import multer from 'multer'; // Import multer

const authRouter = Router();
const upload = multer(); // Initialize multer for form-data parsing

// Use multer middleware for the /login route to handle form-data
authRouter.post('/login', upload.none(), login);

// Keep /refresh as is, assuming it expects JSON or urlencoded
authRouter.post('/refresh', refreshToken);

export { authRouter };

