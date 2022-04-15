FROM node:12.18.3
COPY frontend/package.json ./
RUN npm install
COPY ./frontend .
CMD ["npm", "start"]