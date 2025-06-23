// src/app.ts

import express from 'express';
import cors from 'cors';
import { authRouter } from './routes/auth.routes';
import { usersRouter } from './routes/users.routes';
import { rolesRouter } from './routes/roles.routes';
import { validateRouter } from './routes/validate.routes';
import { setupSwagger } from './swagger';
import { errorMiddleware } from './middlewares/error.middleware';

const app = express();

app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Configure CORS
app.use(
    cors({
        origin: `${process.env.FRONTEND_EXTERNAL_PROTOCOL}://${process.env.FRONTEND_EXTERNAL_HOST}:${process.env.FRONTEND_EXTERNAL_PORT}`,
        credentials: true
    })
);

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
