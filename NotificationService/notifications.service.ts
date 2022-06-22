import { PrismaClient } from "@prisma/client";
import { CreateNotificationDto } from "./dto";

async function createNotification(dto: CreateNotificationDto) {
  const prisma = new PrismaClient();
  return prisma.notification.create({
    data: {
      title: dto.title,
      url: dto.url,
      userEmail: dto.userEmail,
    },
  });
}

export { createNotification };
