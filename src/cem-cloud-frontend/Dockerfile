FROM node:lts AS build
WORKDIR /app

COPY package.json /app/package.json
COPY package-lock.json /app/package-lock.json
RUN npm ci

COPY public/ /app/public
COPY src/ /app/src
COPY tsconfig.json /app/tsconfig.json
RUN npm run build

FROM nginx:alpine
WORKDIR /usr/share/nginx/html
COPY --from=build /app/build/ .
