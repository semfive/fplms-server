FROM node:16

# Create app directory
WORKDIR /usr/src/app

# Install app dependencies
# A wildcard is used to ensure both package.json AND package-lock.json are copied
# where available (npm@5+)
COPY package*.json ./

# RUN npm install
# If you are building your code for production
RUN npm ci --only=production

# Bundle app source
COPY . .

EXPOSE 8080
CMD [ "npm", "run", "start" ]

# FROM node:16.13-alpine
# WORKDIR /app
# ENV NODE_ENV production

# COPY package*.json ./
# RUN npm ci --only=production --quiet

# COPY --chown=node:node --from=builder /app/prisma /app/prisma
# COPY --chown=node:node --from=builder /app/src /app/src

# USER node

# EXPOSE 8080
# CMD ["npm", "run", "start"]
