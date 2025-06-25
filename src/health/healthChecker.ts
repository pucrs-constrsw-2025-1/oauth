import axios from 'axios';
import si from 'systeminformation';

export interface HealthStatus {
    status: 'UP' | 'DOWN';
    details?: any;
}

export interface HealthResponse {
    status: 'UP' | 'DOWN';
    components: {
        keycloak: HealthStatus;
        system: HealthStatus;
    };
}

export class HealthChecker {
    private keycloakBaseUrl: string;

    constructor() {
        this.keycloakBaseUrl = process.env.KEYCLOAK_BASE_URL || 'http://keycloak:8080';
    }

    async checkKeycloak(): Promise<HealthStatus> {
        try {
            // Verifica se o Keycloak está respondendo
            const response = await axios.get(`${this.keycloakBaseUrl}/health`, {
                timeout: 5000
            });
            
            if (response.status === 200) {
                return {
                    status: 'UP',
                    details: 'Keycloak connection is healthy'
                };
            } else {
                return {
                    status: 'DOWN',
                    details: `Keycloak returned status ${response.status}`
                };
            }
        } catch (error) {
            return {
                status: 'DOWN',
                details: `Keycloak connection failed: ${error instanceof Error ? error.message : 'Unknown error'}`
            };
        }
    }

    async checkSystemResources(): Promise<HealthStatus> {
        try {
            const [cpu, mem, disk] = await Promise.all([
                si.currentLoad(),
                si.mem(),
                si.fsSize()
            ]);

            const systemInfo = {
                cpu_usage_percent: Math.round(cpu.currentLoad),
                memory_usage_percent: Math.round((mem.used / mem.total) * 100),
                memory_available_gb: Math.round((mem.available / (1024 ** 3)) * 100) / 100,
                disk_usage_percent: Math.round((disk[0]?.used / disk[0]?.size) * 100) || 0,
                disk_free_gb: Math.round((disk[0]?.available / (1024 ** 3)) * 100) / 100 || 0
            };

            return {
                status: 'UP',
                details: systemInfo
            };
        } catch (error) {
            return {
                status: 'DOWN',
                details: `System resources check failed: ${error instanceof Error ? error.message : 'Unknown error'}`
            };
        }
    }

    async getHealthStatus(): Promise<HealthResponse> {
        const [keycloakHealth, systemHealth] = await Promise.all([
            this.checkKeycloak(),
            this.checkSystemResources()
        ]);

        const overallStatus = (keycloakHealth.status === 'UP' && systemHealth.status === 'UP') ? 'UP' : 'DOWN';

        return {
            status: overallStatus,
            components: {
                keycloak: keycloakHealth,
                system: systemHealth
            }
        };
    }
} 