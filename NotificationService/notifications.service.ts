import { PrismaClient } from "@prisma/client";
import { CreateNotificationDto } from "./dto";

const prisma = new PrismaClient();
async function createNotification(dto: CreateNotificationDto) {
  return prisma.notification.create({
    data: {
      title: dto.title,
      url: dto.url,
      userEmail: dto.userEmail,
    },
  });
}

async function getNotifications(userEmail: string) {
  return prisma.notification.findMany({
    where: {
      userEmail: userEmail,
    },
    orderBy: {
      createdAt: "desc",
    },
    take: 10,
  });
}

export { createNotification, getNotifications };
