# Etapa 1: Build da aplicação
FROM node:20-alpine AS build

# Cria o diretório de trabalho
WORKDIR /app

# Copia apenas os arquivos de dependência primeiro para cache eficiente
COPY package*.json ./

# Instala dependências
RUN npm install

# Copia o restante dos arquivos
COPY . .

# Compila o código TypeScript
RUN npm run build

# Etapa 2: Imagem final, mais leve
FROM node:20-alpine

WORKDIR /app

# Copia apenas os arquivos necessários da etapa anterior
COPY --from=build /app/package*.json ./
COPY --from=build /app/dist ./dist
COPY --from=build /app/node_modules ./node_modules

# Expõe a porta (ajuste conforme sua aplicação)
EXPOSE 3000

# Comando para iniciar o servidor
CMD ["node", "dist/index.js"]