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
import { NotificationsService } from './notifications.service';

@UseGuards(JwtGuard)
@Controller('notifications')
export class NotificationsController {
  constructor(private notificationService: NotificationsService) {}

  @HttpCode(HttpStatus.CREATED)
  @Post()
  async createNotification(
    @User() user: { email: string; role: string },
    @Body() dto: CreateNotificationDto,
  ) {
    return this.notificationService.createNotification(user, dto);
  }
}
