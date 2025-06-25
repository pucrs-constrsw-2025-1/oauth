import { Router, Request, Response } from 'express';
import { register, collectDefaultMetrics, Counter, Histogram } from 'prom-client';
import { HealthChecker } from '../health/healthChecker';

const router = Router();

// Configuração do Prometheus
collectDefaultMetrics();

// Métricas customizadas
const httpRequestsTotal = new Counter({
    name: 'http_requests_total',
    help: 'Total number of HTTP requests',
    labelNames: ['method', 'route', 'status']
});

const httpRequestDuration = new Histogram({
    name: 'http_request_duration_seconds',
    help: 'Duration of HTTP requests in seconds',
    labelNames: ['method', 'route']
});

let healthChecker: HealthChecker;

export function initializeActuator() {
    healthChecker = new HealthChecker();
}

// Middleware para métricas
router.use((req: Request, res: Response, next) => {
    const start = Date.now();
    
    res.on('finish', () => {
        const duration = (Date.now() - start) / 1000;
        httpRequestsTotal.inc({ method: req.method, route: req.route?.path || req.path, status: res.statusCode });
        httpRequestDuration.observe({ method: req.method, route: req.route?.path || req.path }, duration);
    });
    
    next();
});

// Health check endpoint
router.get('/health', async (req: Request, res: Response) => {
    try {
        const healthStatus = await healthChecker.getHealthStatus();
        
        if (healthStatus.status === 'DOWN') {
            return res.status(503).json(healthStatus);
        }
        
        res.json(healthStatus);
    } catch (error) {
        res.status(503).json({
            status: 'DOWN',
            error: error instanceof Error ? error.message : 'Unknown error'
        });
    }
});

// Liveness probe
router.get('/health/liveness', (req: Request, res: Response) => {
    res.json({ status: 'UP' });
});

// Readiness probe
router.get('/health/readiness', async (req: Request, res: Response) => {
    try {
        const healthStatus = await healthChecker.getHealthStatus();
        
        if (healthStatus.status === 'DOWN') {
            return res.status(503).json(healthStatus);
        }
        
        res.json({ status: 'UP' });
    } catch (error) {
        res.status(503).json({
            status: 'DOWN',
            error: error instanceof Error ? error.message : 'Unknown error'
        });
    }
});

// Info endpoint
router.get('/info', (req: Request, res: Response) => {
    res.json({
        app: {
            name: 'OAuth Microservice',
            version: '1.0.0',
            description: 'Microservice for OAuth authentication and authorization'
        },
        build: {
            time: new Date().toISOString(),
            version: '1.0.0'
        },
        git: {
            commit: {
                time: new Date().toISOString()
            }
        }
    });
});

// Metrics endpoint
router.get('/metrics', async (req: Request, res: Response) => {
    try {
        res.set('Content-Type', register.contentType);
        res.end(await register.metrics());
    } catch (error) {
        res.status(500).json({ error: 'Failed to generate metrics' });
    }
});

// Prometheus endpoint
router.get('/prometheus', async (req: Request, res: Response) => {
    try {
        res.set('Content-Type', register.contentType);
        res.end(await register.metrics());
    } catch (error) {
        res.status(500).json({ error: 'Failed to generate metrics' });
    }
});

// Environment info
router.get('/env', (req: Request, res: Response) => {
    res.json({
        activeProfiles: ['default'],
        propertySources: [
            {
                name: 'systemEnvironment',
                properties: {
                    KEYCLOAK_BASE_URL: process.env.KEYCLOAK_BASE_URL,
                    KEYCLOAK_REALM: process.env.KEYCLOAK_REALM,
                    KEYCLOAK_CLIENT_ID: process.env.KEYCLOAK_CLIENT_ID,
                    OAUTH_INTERNAL_API_PORT: process.env.OAUTH_INTERNAL_API_PORT,
                    NODE_ENV: process.env.NODE_ENV
                }
            }
        ]
    });
});

// Endpoint mappings
router.get('/mappings', (req: Request, res: Response) => {
    res.json({
        contexts: {
            oauth: {
                mappings: {
                    dispatcherServlet: {
                        details: {
                            handlerMethods: {
                                'GET /actuator/health': 'Health check endpoint',
                                'GET /actuator/health/liveness': 'Liveness probe',
                                'GET /actuator/health/readiness': 'Readiness probe',
                                'GET /actuator/info': 'Application info',
                                'GET /actuator/metrics': 'Prometheus metrics',
                                'GET /actuator/env': 'Environment info',
                                'GET /actuator/mappings': 'Endpoint mappings'
                            }
                        }
                    }
                }
            }
        }
    });
});

export default router; 