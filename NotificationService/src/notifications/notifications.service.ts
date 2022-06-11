import { Injectable } from '@nestjs/common';
import { PrismaService } from 'src/prisma/prisma.service';
import { CreateNotificationDto } from './dto';

@Injectable()
export class NotificationsService {
  constructor(private readonly prisma: PrismaService) {}
  async getNotifications(userEmail: string) {
    return await this.prisma.notification.findMany({
      where: {
        userEmail: userEmail,
      },
      orderBy: {
        createdAt: 'desc',
      },
    });
  }

  async createNotification(
    user: { email: string; role: string },
    dto: CreateNotificationDto,
  ) {
    return await this.prisma.notification.create({
      data: {
        title: dto.title,
        url: dto.url,
        userEmail: user.email,
      },
    });
  }
}
