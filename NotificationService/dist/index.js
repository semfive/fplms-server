"use strict";
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
Object.defineProperty(exports, "__esModule", { value: true });
const http = require("http");
const path = require("path");
const dotenv = require("dotenv");
const socket_io_1 = require("socket.io");
const constants_1 = require("./constants");
const notifications_logger_1 = require("./notifications.logger");
const jwt = require("jsonwebtoken");
const { validateToken } = require("./middlewares/auth.middleware");
const { handleCreateNotification, handleGetNotifications, } = require("./notifications.controller");
dotenv.config({
    path: path.join(__dirname, `../.env.${process.env.NODE_ENV}`),
});
const server = http.createServer((req, res) => __awaiter(void 0, void 0, void 0, function* () {
    let requestErrorMessage = null;
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
        yield handleCreateNotification({
            req,
            res,
            io,
            users,
            requestErrorMessage,
        });
        //   );
        // }
        res.on("finish", () => (0, notifications_logger_1.logger)({ req, res, requestErrorMessage }));
        res.on("close", () => (0, notifications_logger_1.logger)({ req, res, CLIENT_ABORTED: constants_1.CLIENT_ABORTED }));
        res.on("error", ({ message }) => (0, notifications_logger_1.logger)({ req, res, message }));
    }
}));
const io = new socket_io_1.Server(server, {
    cors: {
        origin: "http://localhost:3000",
        methods: ["GET", "POST"],
        allowedHeaders: ["authorization"],
        credentials: true,
    },
});
const users = new Set();
io.on("connection", (socket) => __awaiter(void 0, void 0, void 0, function* () {
    console.log("Made socket connection: " + socket.id);
    try {
        const token = socket.handshake.headers["authorization"];
        var decoded = jwt.verify(token, process.env.JWT_SECRET);
        var newUser = {
            email: decoded["email"],
            id: socket.id,
        };
        users.add(newUser);
    }
    catch (err) {
        socket.disconnect();
    }
    socket.on("notifications", (data) => __awaiter(void 0, void 0, void 0, function* () {
        const notifications = yield handleGetNotifications(newUser["email"]);
        io.emit("notifications", notifications);
    }));
    socket.on("disconnect", () => {
        users.delete(newUser);
    });
}));
server.listen(process.env.SERVER_PORT || 8080, () => {
    console.log(`Server is running in ${process.env.NODE_ENV} mode on port ${process.env.SERVER_PORT}.`);
});
