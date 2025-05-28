# ─── Stage 1: builder (com devDependencies) ────────────────────────────────
FROM node:20-alpine AS builder
WORKDIR /app

# Copia só o package.json e o lockfile, para cache de dependências
COPY package.json package-lock.json ./

# Instala TODAS as deps, incluindo dev (typescript, etc)
RUN npm ci

# Copia o restante do código (sem node_modules/dist graças ao .dockerignore)
COPY . .

# Compila o TypeScript
RUN npm run build



# ─── Stage 2: produção (só dependencies e build result) ───────────────────
FROM node:20-alpine AS production
WORKDIR /app

# Instala o curl
RUN apk add --no-cache curl

# Copia só o package.json e lockfile
COPY package.json package-lock.json ./

# Instala apenas as deps de produção
RUN npm ci --omit=dev
RUN npm install --save-dev @types/node
RUN npm install axios express
RUN npm install --save-dev @types/express

# Traz o build compilado do builder
COPY --from=builder /app/dist ./dist

EXPOSE 3000
CMD ["node", "dist/index.js"]
