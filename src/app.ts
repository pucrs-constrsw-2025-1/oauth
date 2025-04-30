import express from 'express';
import { authRouter } from './routes/auth.routes';
import { usersRouter } from './routes/users.routes';
// import { rolesRouter } from './routes/roles.routes'; // Removido
// import { validateRouter } from './routes/validate.routes'; // Removido
import { errorMiddleware } from './middlewares/error.middleware';

const app = express();

app.use(express.json());
app.use(express.urlencoded({ extended: true })); // Adicionado para suportar x-www-form-urlencoded se necessário

// Montar authRouter na raiz para ter /login e /refresh
app.use('/', authRouter);
app.use('/users', usersRouter);
// app.use('/roles', rolesRouter); // Removido
// app.use('/auth', validateRouter); // Removido - rota /validate removida

// Rota de health check movida do index.ts para cá
app.get('/health', (req, res) => {
  res.send('OAuth service is healthy');
});

// Middleware de erro deve ser o último
app.use(errorMiddleware);

export default app; // esta linha para exportar o app configurado

