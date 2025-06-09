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

# Instala curl para a healthcheck
RUN apk add --no-cache curl

WORKDIR /app

# Copia só o package.json e lockfile
COPY package.json package-lock.json ./

# Instala apenas as deps de produção
RUN npm ci --omit=dev

# Traz o build compilado do builder
COPY --from=builder /app/dist ./dist

EXPOSE 3000
CMD ["node", "dist/index.js"]
