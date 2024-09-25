# Stage 1: Build frontend
FROM node:22-alpine3.19 as build
WORKDIR /app
COPY package*.json /app/
RUN npm install -g @angular/cli
# Install git before npm install (for mdb)
RUN apk add --no-cache git
RUN npm install
COPY . .
RUN npm run build

# Stage 2: Serve it using Ngnix
FROM nginx:1.25.3-alpine-slim
COPY nginx.conf /etc/nginx/nginx.conf
COPY --from=build /app/dist/dms-frontend/browser /usr/share/nginx/html
CMD ["nginx", "-g", "daemon off;"]
