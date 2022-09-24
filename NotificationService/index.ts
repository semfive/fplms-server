const http = require("http");
const path = require("path");
const dotenv = require("dotenv");
import { IncomingMessage, ServerResponse } from "http";
import { Server } from "socket.io";
import { validateOrigin } from "./auth.middleware";
import { CLIENT_ABORTED } from "./constants";
import { logger } from "./notifications.logger";
const jwt = require("jsonwebtoken");

const { validateToken } = require("./middlewares/auth.middleware");
const {
  handleCreateNotification,
  handleGetNotifications,
} = require("./notifications.controller");

dotenv.config({
  path: path.join(__dirname, `../.env.${process.env.NODE_ENV}`),
});

const server = http.createServer(
  async (req: IncomingMessage, res: ServerResponse) => {
    let requestErrorMessage: string | null = null;
    const headers = {
      "Access-Control-Allow-Origin": [
        process.env.DISCUSSION_SERVICE,
        process.env.MANAGEMENT_SERVICE,
      ],
      "Access-Control-Allow-Methods": "OPTIONS, POST, GET",
      "Access-Control-Max-Age": 2592000,
    };

    if (req.method === "OPTIONS") {
      res.writeHead(204, headers);
      res.end();
      return;
    }

    if (req.url === "/api/notification" && req.method === "POST") {
      // await validateOrigin(
      //   req,
      //   res,
      //   async () =>
      //     await handleCreateNotification({
      //       req,
      //       res,
      //       io,
      //       users,
      //       requestErrorMessage,
      //     })
      // );
      // await validateOrigin(
      //   req,
      //   res,
      //   async () =>
          await handleCreateNotification({
            req,
            res,
            io,
            users,
            requestErrorMessage,
          })
    //   );
    // }

    res.on("finish", () => logger({ req, res, requestErrorMessage }));
    res.on("close", () => logger({ req, res, CLIENT_ABORTED }));
    res.on("error", ({ message }: err) => logger({ req, res, message }));
  }
);

const io = new Server(server, {
  cors: {
    origin: "http://localhost:3000",
    methods: ["GET", "POST"],
    allowedHeaders: ["authorization"],
    credentials: true,
  },
});
const users = new Set();
io.on("connection", async (socket) => {
  console.log("Made socket connection: " + socket.id);

  try {
    const token = socket.handshake.headers["authorization"];
    var decoded = jwt.verify(token, process.env.JWT_SECRET);
  } catch (err) {
    socket.disconnect();
  }
  
  var newUser = {
    email: decoded["email"],
    id: socket.id,
  };

  users.add(newUser);

  // socket.on("notifications", async (data) => {
    const notifications = await handleGetNotifications(newUser["email"]);
    // io.emit("notifications", notifications);
    socket.emit("notifications", notifications);
  // });

  socket.on("disconnect", () => {
    users.delete(newUser);
  });
});

server.listen(process.env.SERVER_PORT || 8080, () => {
  console.log(
    `Server is running in ${process.env.NODE_ENV} mode on port ${process.env.SERVER_PORT}.`
  );
});
