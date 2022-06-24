const http = require("http");
const path = require("path");
const dotenv = require("dotenv");
import { Server } from "socket.io";
const jwt = require("jsonwebtoken");

const { validateToken } = require("./middlewares/auth.middleware");
const {
  handleCreateNotification,
  handleGetNotifications,
} = require("./notifications.controller");

dotenv.config({
  path: path.join(__dirname, `../.env.${process.env.NODE_ENV}`),
});

const server = http.createServer(async (req, res) => {
  if (req.url === "/api/notification" && req.method === "POST") {
    await validateToken(req, res, () =>
      handleCreateNotification(req, res, io, users)
    );
  }
});

const io = new Server(server, {});
const users = new Set();
io.on("connection", async (socket) => {
  console.log("Made socket connection: " + socket.id);

  try {
    const token = socket.handshake.headers["authorization"];
    var decoded = jwt.verify(token, process.env.JWT_SECRET);

    var newUser = {
      email: decoded["email"],
      id: socket.id,
    };

    users.add(newUser);
  } catch (err) {
    socket.disconnect();
  }

  socket.on("notifications", async (data) => {
    const notifications = await handleGetNotifications(newUser["email"]);
    io.emit("notifications", notifications);
  });

  socket.on("disconnect", () => {
    users.delete(newUser);
  });
});

server.listen(process.env.SERVER_PORT || 8080, () => {
  console.log(
    `Server is running in ${process.env.NODE_ENV} mode on port ${process.env.SERVER_PORT}.`
  );
});
