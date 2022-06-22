const http = require("http");
const path = require("path");
const dotenv = require("dotenv");

const { validateToken } = require("./middlewares/auth.middleware");
const { handleCreateNotification } = require("./notifications.controller");

dotenv.config({
  path: path.join(__dirname, `../.env.${process.env.NODE_ENV}`),
});

const server = http.createServer(async (req, res) => {
  if (req.url === "/api/notification" && req.method === "POST") {
    await validateToken(req, res, () => handleCreateNotification(req, res));
  }
});

server.listen(process.env.SERVER_PORT || 8080, () => {
  console.log(
    `Server is running in ${process.env.NODE_ENV} mode on port ${process.env.SERVER_PORT}.`
  );
});
