FROM node:alpine as build
COPY package*.json ./
WORKDIR /app
RUN npm install
RUN npm install ng-otp-input @angular/material
COPY . .
RUN  npm run build
FROM nginx:stable
COPY --from=build /app/dist/cantine/  /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf