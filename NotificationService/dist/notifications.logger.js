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
exports.logger = void 0;
const constants_1 = require("./constants");
function logger({ req, res, errorMessage }) {
    return __awaiter(this, void 0, void 0, function* () {
        const { rawHeaders, httpVersion, method, socket, url } = req;
        const { remoteAddress, remoteFamily } = socket;
        const { statusCode, statusMessage } = res;
        const headers = res.getHeaders();
        if (errorMessage === "") {
            console.log(JSON.stringify({
                timestamp: Date.now(),
                //   processingTime: Date.now() - requestStart,
                rawHeaders,
                // data,
                errorMessage,
                httpVersion,
                method,
                remoteAddress,
                remoteFamily,
                url,
                response: {
                    statusCode,
                    statusMessage,
                    headers,
                },
            }));
        }
        else if (errorMessage === constants_1.CLIENT_ABORTED) {
            console.log(JSON.stringify({
                timestamp: Date.now(),
                //   processingTime: Date.now() - requestStart,
                rawHeaders,
                // data,
                errorMessage,
                httpVersion,
                method,
                remoteAddress,
                remoteFamily,
                url,
                response: {
                    statusCode,
                    statusMessage,
                    headers,
                },
            }));
        }
        else {
            console.log(JSON.stringify({
                timestamp: Date.now(),
                //   processingTime: Date.now() - requestStart,
                rawHeaders,
                // data,
                errorMessage,
                httpVersion,
                method,
                remoteAddress,
                remoteFamily,
                url,
                response: {
                    statusCode,
                    statusMessage,
                    headers,
                },
            }));
        }
    });
}
exports.logger = logger;
