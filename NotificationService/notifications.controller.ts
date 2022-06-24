import { CreateNotificationDto, GetNotificationDto } from "./dto";
import { createNotification, getNotifications } from "./notifications.service";

async function handleCreateNotification(req, res, io, users) {
  let data = "";
  let dto: CreateNotificationDto = {};
  req.on("data", (chunk) => {
    data += chunk;
  });

  req.on("end", async () => {
    dto = JSON.parse(data);
    if (
      dto.title.length == 0 ||
      dto.title === undefined ||
      typeof dto.title != "string"
    ) {
      res.writeHead(400);
      res.write("Title is missing.");
      return res.end();
    }
    if (
      dto.url.length == 0 ||
      dto.url === undefined ||
      typeof dto.url != "string"
    ) {
      res.writeHead(400);
      res.write("URL is missing.");
      return res.end();
    }

    dto.userEmail = req.headers["user"]["email"];

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
    res.write("Create notification failed.");
    return res.end();
  });
}

async function handleGetNotifications(userEmail: string) {
  return await getNotifications(userEmail);
}

export { handleCreateNotification, handleGetNotifications };
