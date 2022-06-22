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
const http = require("http");
const path = require("path");
const dotenv = require("dotenv");
const { validateToken } = require("./middlewares/auth.middleware");
const { handleCreateNotification } = require("./notifications.controller");
dotenv.config({
    path: path.join(__dirname, `../.env.${process.env.NODE_ENV}`),
});
const server = http.createServer((req, res) => __awaiter(void 0, void 0, void 0, function* () {
    if (req.url === "/api/notification" && req.method === "POST") {
        yield validateToken(req, res, () => handleCreateNotification(req, res));
    }
}));
server.listen(process.env.SERVER_PORT || 8080, () => {
    console.log(`Server is running in ${process.env.NODE_ENV} mode on port ${process.env.SERVER_PORT}.`);
});
