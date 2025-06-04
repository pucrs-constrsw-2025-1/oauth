# ─── Stage 1: builder (com devDependencies) ────────────────────────────────
FROM node:20-alpine AS builder
WORKDIR /app

# Copia só o package.json e o lockfile, para cache de dependências
COPY package.json package-lock.json ./

# Instala TODAS as deps, incluindo dev (typescript, etc)
RUN npm ci
# Instala versões específicas do TypeScript e ts-node
RUN npm install typescript@4.9.5 ts-node@10.9.1 ts-node-dev@2.0.0

# Copia o restante do código (sem node_modules/dist graças ao .dockerignore)
COPY . .

# Compila o TypeScript com source maps
RUN ./node_modules/.bin/tsc



# ─── Stage 2: desenvolvimento (com todas as dependências) ───────────────────
FROM node:20-alpine AS development
WORKDIR /app

# Instala o curl
RUN apk add --no-cache curl

# Copia package.json e lockfile
COPY package.json package-lock.json ./

# Instala TODAS as dependências, incluindo as de desenvolvimento
RUN npm ci
# Instala versões específicas do TypeScript e ts-node
RUN npm install typescript@4.9.5 ts-node@10.9.1 ts-node-dev@2.0.0

# Copia os arquivos fonte e compilados
COPY --from=builder /app/dist ./dist
COPY --from=builder /app/src ./src

# Expõe as portas da aplicação e de debug
EXPOSE 3000 9229

# Comando para executar a aplicação com suporte a debug
CMD ["./node_modules/.bin/ts-node-dev", "--respawn", "--transpile-only", "--inspect=0.0.0.0:9229", "src/index.ts"]



# ─── Stage 3: produção (só dependencies e build result) ───────────────────
FROM node:20-alpine AS production
WORKDIR /app

# Instala o curl
RUN apk add --no-cache curl

# Copia só o package.json e lockfile
COPY package.json package-lock.json ./

# Instala apenas as deps de produção
RUN npm ci --omit=dev

# Traz o build compilado do builder
COPY --from=builder /app/dist ./dist

# Expõe as portas da aplicação e de debug
EXPOSE 3000 9229

# Comando para executar a aplicação com suporte a debug
CMD ["node", "--inspect=0.0.0.0:9229", "dist/index.js"]
