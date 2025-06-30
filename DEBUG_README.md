# Debug do Serviço OAuth

## Configuração de Debug

O serviço OAuth está configurado para suportar debug remoto via Node.js Inspector.

### Portas de Debug

- **Porta Interna**: 9229 (dentro do container)
- **Porta Externa**: 8281 (mapeada do host)

### Configuração do VS Code

O arquivo `.vscode/launch.json` já está configurado com a seguinte configuração:

```json
{
    "type": "node",
    "name": "OAuth Service",
    "request": "attach",
    "port": 8281,
    "address": "localhost",
    "localRoot": "${workspaceFolder}/backend/oauth/src",
    "remoteRoot": "/app/src",
    "protocol": "inspector",
    "restart": true,
    "sourceMaps": true
}
```

### Como Usar

1. **Iniciar o serviço**:
   ```bash
   docker-compose up -d oauth
   ```

2. **Aguardar o serviço estar saudável**:
   ```bash
   docker-compose ps oauth
   ```

3. **Conectar o debugger no VS Code**:
   - Abrir a aba "Run and Debug" (Ctrl+Shift+D)
   - Selecionar "OAuth Service" na lista
   - Clicar no botão de play (▶️) ou pressionar F5

4. **Definir breakpoints**:
   - Abrir qualquer arquivo TypeScript do projeto em `backend/oauth/src`
   - Clicar na margem esquerda para definir breakpoints
   - Fazer uma requisição para o endpoint desejado

### Endpoints Disponíveis

- **Health Check**: `GET http://localhost:8181/actuator/health`
- **API Base**: `http://localhost:8181`
- **Documentação**: `http://localhost:8181/api-docs`
- **Swagger UI**: `http://localhost:8181/swagger-ui`

### Endpoints Principais

- **Autenticação**: `POST http://localhost:8181/auth/login`
- **Usuários**: `GET/POST/PUT/DELETE http://localhost:8181/users`
- **Roles**: `GET/POST/PUT/DELETE http://localhost:8181/roles`
- **Validação**: `POST http://localhost:8181/auth/validate`

### Troubleshooting

#### Problema: "Connection refused"
- Verificar se o container está rodando: `docker-compose ps oauth`
- Verificar se a porta 8281 está mapeada: `docker port oauth`
- Verificar logs: `docker-compose logs oauth`

#### Problema: "Source not found"
- Verificar se o `localRoot` no launch.json está correto
- Verificar se o `remoteRoot` está correto: `/app/src`
- Verificar se os source maps estão habilitados

#### Problema: Debugger não para nos breakpoints
- Verificar se o breakpoint está definido no arquivo correto
- Verificar se o código está sendo executado (fazer uma requisição HTTP)
- Verificar se não há erros de compilação TypeScript

### Configuração do Dockerfile

O Dockerfile está configurado com:

```dockerfile
# Expor porta de debug
EXPOSE 3000 9229

# Configurar Node.js para debug remoto
ENV NODE_OPTIONS="--inspect=0.0.0.0:9229"

CMD ["npm", "run", "dev"]
```

### Variáveis de Ambiente

As seguintes variáveis de ambiente são usadas para debug:

- `OAUTH_INTERNAL_DEBUG_PORT=9229` (porta interna)
- `OAUTH_EXTERNAL_DEBUG_PORT=8281` (porta externa)
- `OAUTH_INTERNAL_API_PORT=3000` (porta da API)
- `OAUTH_EXTERNAL_API_PORT=8181` (porta externa da API)

### Logs de Debug

Para ver logs detalhados do debug:

```bash
docker-compose logs oauth | grep -i debug
```

### Reiniciar com Debug

Se precisar reiniciar o serviço com debug:

```bash
docker-compose restart oauth
```

### Verificar Status do Debug

Para verificar se o debug está ativo:

```bash
docker exec oauth ps aux | grep node
```

Deve mostrar o Node.js com as opções de debug: `--inspect=0.0.0.0:9229`

### Scripts NPM

- `npm run dev`: Inicia o servidor em modo desenvolvimento com debug
- `npm run build`: Compila o TypeScript
- `npm run start`: Inicia o servidor em produção

### Dependências

- Node.js 18+
- Express.js
- TypeScript
- Swagger UI Express
- CORS
- Helmet

### Estrutura do Projeto

```
src/
├── app.ts              # Configuração principal do Express
├── controllers/        # Controladores da API
├── routes/            # Definição de rotas
├── middlewares/       # Middlewares customizados
├── swagger.ts         # Configuração do Swagger
└── index.ts           # Ponto de entrada
```

### Hot Reload

O serviço está configurado com hot reload via `nodemon`:

```json
{
    "scripts": {
        "dev": "nodemon --inspect=0.0.0.0:9229 src/index.ts"
    }
}
```

### Source Maps

Para debug adequado, certifique-se de que os source maps estão habilitados no `tsconfig.json`:

```json
{
    "compilerOptions": {
        "sourceMap": true,
        "outDir": "./dist"
    }
}
``` 