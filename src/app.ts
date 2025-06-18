// src/app.ts

import express from 'express';
import cors from 'cors';
import dotenv from 'dotenv';
import { authRouter } from './routes/auth.routes';
import { usersRouter } from './routes/users.routes';
import { rolesRouter } from './routes/roles.routes';
import { validateRouter } from './routes/validate.routes';
import { setupSwagger } from './swagger';
import { errorMiddleware } from './middlewares/error.middleware';

dotenv.config();

const app = express();

// Configure CORS
const corsOptions = {
  origin: function (origin: string | undefined, callback: (err: Error | null, allow?: boolean) => void) {
    const allowedOrigins = [
      'http://localhost:8080', 
      'http://127.0.0.1:8080', 
      'http://localhost:3000', 
      'http://127.0.0.1:3000'
    ];

    if (!origin || allowedOrigins.includes(origin)) {
      callback(null, true);
    } else {
      console.log('Blocked by CORS:', origin);
      callback(new Error('Not allowed by CORS'));
    }
  },
  methods: ['GET', 'POST', 'PUT', 'DELETE', 'OPTIONS'],
  allowedHeaders: ['Content-Type', 'Authorization', 'Accept'],
  credentials: true,
  preflightContinue: false,
  optionsSuccessStatus: 204
};

app.use(cors(corsOptions));

app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Mount your routers
app.use('/', authRouter);
app.use('/users', usersRouter);
app.use('/roles', rolesRouter);
app.use('/auth', validateRouter);

// Serve Swagger docs
setupSwagger(app);

// Healthcheck
app.get('/health', (req, res) => res.send('OAuth service is healthy'));

// Error handler
app.use(errorMiddleware);

export default app;
