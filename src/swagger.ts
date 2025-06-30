// src/swagger.ts
import { Express } from 'express';
import swaggerJsdoc from 'swagger-jsdoc';
import swaggerUi from 'swagger-ui-express';
import path from 'path';

const swaggerDefinition = {
  openapi: '3.0.0',
  info: { title: 'OAuth API', version: '1.0.0', description: 'Documentation for your OAuth service' },
  servers: [{ url: process.env.BASE_URL || 'http://localhost:' + process.env.OAUTH_EXTERNAL_API_PORT }],
  components: {
    securitySchemes: {
      bearerAuth: { type: 'http', scheme: 'bearer', bearerFormat: 'JWT' }
    }
  }
};

const options = {
  swaggerDefinition,
  apis: [
    // Em dev, lê direto dos TS
    path.resolve(__dirname, './routes/*.ts'),
    path.resolve(__dirname, './controllers/*.ts'),
    // Em produção, lê dos .js
    path.resolve(__dirname, '../dist/routes/*.js'),
    path.resolve(__dirname, '../dist/controllers/*.js'),
  ]
};

const swaggerSpec = swaggerJsdoc(options);

export function setupSwagger(app: Express) {
  // Endpoint para JSON da documentação
  app.get('/api-docs', (req, res) => {
    res.setHeader('Content-Type', 'application/json');
    res.send(swaggerSpec);
  });
  
  // Interface Swagger UI
  app.use('/swagger-ui', swaggerUi.serve, swaggerUi.setup(swaggerSpec));
}
