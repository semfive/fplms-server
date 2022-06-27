import { IncomingMessage, ServerResponse } from "http";
import { Server } from "socket.io";
import {
  CREATE_NOTIFICATION_FAILED,
  TITLE_IS_MISSING,
  URL_IS_MISSING,
  USER_EMAIL_IS_MISSING,
} from "./constants";
import { CreateNotificationDto, GetNotificationDto } from "./dto";
import { createNotification, getNotifications } from "./notifications.service";

type Dependencies = {
  req: IncomingMessage;
  res: ServerResponse;
  io: Server;
  users: Set<{
    email: string;
    id: string;
  }>;
  requestErrorMessage: string | null;
};

async function handleCreateNotification({
  req,
  res,
  io,
  users,
  requestErrorMessage,
}: Dependencies) {
  // const requestStart = Date.now();
  let data = [];
  let dto: CreateNotificationDto = {};

  const getChunk = (chunk) => data.push(chunk);
  const getError = (error) => {
    requestErrorMessage = error.message;
  };

  req.on("data", getChunk);

  req.on("error", getError);

  req.on("end", async () => {
    data = Buffer.concat(data).toString();
    dto = JSON.parse(data);
    if (
      dto.title.length == 0 ||
      dto.title === undefined ||
      typeof dto.title != "string"
    ) {
      res.writeHead(400);
      res.write(TITLE_IS_MISSING);
      return res.end();
    }
    if (
      dto.url.length == 0 ||
      dto.url === undefined ||
      typeof dto.url != "string"
    ) {
      res.writeHead(400);
      res.write(URL_IS_MISSING);
      return res.end();
    }
    if (
      dto.userEmail.length == 0 ||
      dto.userEmail === undefined ||
      typeof dto.userEmail != "string"
    ) {
      res.writeHead(400);
      res.write(USER_EMAIL_IS_MISSING);
      return res.end();
    }

    // dto.userEmail = req.headers["user"]["email"];

    const notification = await createNotification(dto);
    if (notification) {
      for (const e of users) {
        if (e["email"] === notification.userEmail) {
          var socketId = e["id"];
          break;
        }
      }

      let dto: GetNotificationDto = notification;
      delete dto["updatedAt"];
      delete dto["id"];
      delete dto["userEmail"];

      io.to(socketId).emit("notifications", JSON.stringify(dto));
      res.writeHead(201);
      res.write(JSON.stringify(notification));
      return res.end();
    }
    res.writeHead(500);
    res.write(CREATE_NOTIFICATION_FAILED);
    return res.end();
  });
}

async function handleGetNotifications(userEmail: string) {
  return await getNotifications(userEmail);
}

export { handleCreateNotification, handleGetNotifications };
