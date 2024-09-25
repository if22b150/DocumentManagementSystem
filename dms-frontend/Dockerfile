# Stage 1: Build frontend
FROM node:21-alpine3.18 as build
WORKDIR /app
COPY package*.json /app/
RUN npm install -g @angular/cli
RUN npm install
COPY dms-frontend .
RUN npm run build --prod

# Stage 2: Serve it using Ngnix
FROM nginx:1.25.3-alpine-slim
COPY nginx.conf /etc/nginx/nginx.conf
COPY --from=build /app/dist/dms-frontend/browser /usr/share/nginx/html
CMD ["nginx", "-g", "daemon off;"]
