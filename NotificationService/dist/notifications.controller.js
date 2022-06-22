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
exports.handleCreateNotification = void 0;
const notifications_service_1 = require("./notifications.service");
function handleCreateNotification(req, res) {
    return __awaiter(this, void 0, void 0, function* () {
        let data = "";
        let dto = {};
        req.on("data", (chunk) => {
            data += chunk;
        });
        req.on("end", () => __awaiter(this, void 0, void 0, function* () {
            dto = JSON.parse(data);
            if (dto.title.length == 0 ||
                dto.title === undefined ||
                typeof dto.title != "string") {
                res.writeHead(400);
                res.write("Title is missing.");
                return res.end();
            }
            if (dto.url.length == 0 ||
                dto.url === undefined ||
                typeof dto.url != "string") {
                res.writeHead(400);
                res.write("URL is missing.");
                return res.end();
            }
            dto.userEmail = req.headers["user"]["email"];
            const notification = yield (0, notifications_service_1.createNotification)(dto);
            if (notification) {
                res.writeHead(201);
                res.write(JSON.stringify(notification));
                return res.end();
            }
            res.writeHead(500);
            res.write("Create notification failed.");
            return res.end();
        }));
    });
}
exports.handleCreateNotification = handleCreateNotification;
