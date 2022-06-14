import {
  Body,
  Controller,
  HttpCode,
  HttpStatus,
  Post,
  UseGuards,
} from '@nestjs/common';
import { User } from 'src/decorator';
import { JwtGuard } from 'src/guard';
import { CreateNotificationDto } from './dto';
import { NotificationsGateway } from './notifications.gateway';
import { NotificationsService } from './notifications.service';

@UseGuards(JwtGuard)
@Controller('notifications')
export class NotificationsController {
  constructor(
    private notificationService: NotificationsService,
    private readonly notificationGateway: NotificationsGateway,
  ) {}

  @HttpCode(HttpStatus.CREATED)
  @Post()
  async createNotification(
    @User() user: { email: string; role: string },
    @Body() dto: CreateNotificationDto,
  ) {
    const notification = await this.notificationService.createNotification(
      user,
      dto,
    );

    this.notificationGateway.server.emit('new-notification', notification);
    return notification;
  }
}
