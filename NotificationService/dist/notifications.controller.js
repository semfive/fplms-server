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
exports.handleGetNotifications = exports.handleCreateNotification = void 0;
const constants_1 = require("./constants");
const notifications_service_1 = require("./notifications.service");
function handleCreateNotification({ req, res, io, users, requestErrorMessage, }) {
    return __awaiter(this, void 0, void 0, function* () {
        // const requestStart = Date.now();
        let data = [];
        let dto = {};
        const getChunk = (chunk) => data.push(chunk);
        const getError = (error) => {
            requestErrorMessage = error.message;
        };
        req.on("data", getChunk);
        req.on("error", getError);
        req.on("end", () => __awaiter(this, void 0, void 0, function* () {
            data = Buffer.concat(data).toString();
            dto = JSON.parse(data);
            if (dto.title.length == 0 ||
                dto.title === undefined ||
                typeof dto.title != "string") {
                res.writeHead(400);
                res.write(constants_1.TITLE_IS_MISSING);
                return res.end();
            }
            if (dto.url.length == 0 ||
                dto.url === undefined ||
                typeof dto.url != "string") {
                res.writeHead(400);
                res.write(constants_1.URL_IS_MISSING);
                return res.end();
            }
            if (dto.userEmail.length == 0 ||
                dto.userEmail === undefined ||
                typeof dto.userEmail != "string") {
                res.writeHead(400);
                res.write(constants_1.USER_EMAIL_IS_MISSING);
                return res.end();
            }
            // dto.userEmail = req.headers["user"]["email"];
            const notification = yield (0, notifications_service_1.createNotification)(dto);
            if (notification) {
                for (const e of users) {
                    if (e["email"] === notification.userEmail) {
                        var socketId = e["id"];
                        break;
                    }
                }
                let dto = notification;
                delete dto["updatedAt"];
                delete dto["id"];
                delete dto["userEmail"];
                io.to(socketId).emit("notifications", JSON.stringify(dto));
                res.writeHead(201);
                res.write(JSON.stringify(notification));
                return res.end();
            }
            res.writeHead(500);
            res.write(constants_1.CREATE_NOTIFICATION_FAILED);
            return res.end();
        }));
    });
}
exports.handleCreateNotification = handleCreateNotification;
function handleGetNotifications(userEmail) {
    return __awaiter(this, void 0, void 0, function* () {
        return yield (0, notifications_service_1.getNotifications)(userEmail);
    });
}
exports.handleGetNotifications = handleGetNotifications;
