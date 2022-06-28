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
exports.validateOrigin = exports.validateToken = void 0;
const jwt = require("jsonwebtoken");
function validateToken({ req, res, next }) {
    return __awaiter(this, void 0, void 0, function* () {
        const token = req.headers["authorization"];
        if (!token) {
            res.writeHead(401);
            res.write("Token not found.");
            return res.end();
        }
        try {
            const decoded = jwt.verify(token, process.env.JWT_SECRET);
            req.headers["user"] = decoded;
        }
        catch (err) {
            res.writeHead(401);
            res.write("Invalid token.");
            return res.end();
        }
        return next();
    });
}
exports.validateToken = validateToken;
function validateOrigin(req, res, next) {
    return __awaiter(this, void 0, void 0, function* () {
        const origin = req.headers["host"];
        if ([process.env.DISCUSSION_SERVICE, process.env.MANAGEMENT_SERVICE].indexOf(origin) === -1) {
            res.writeHead(403);
            res.write("Access to resource denied.");
            return res.end();
        }
        return next();
    });
}
exports.validateOrigin = validateOrigin;
