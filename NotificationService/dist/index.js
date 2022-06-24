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
const jwt = require("jsonwebtoken");
const { validateToken } = require("./middlewares/auth.middleware");
const { handleCreateNotification, handleGetNotifications, } = require("./notifications.controller");
dotenv.config({
    path: path.join(__dirname, `../.env.${process.env.NODE_ENV}`),
});
const server = http.createServer((req, res) => __awaiter(void 0, void 0, void 0, function* () {
    if (req.url === "/api/notification" && req.method === "POST") {
        yield validateToken(req, res, () => handleCreateNotification(req, res, io, users));
    }
}));
const io = new socket_io_1.Server(server, {});
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
