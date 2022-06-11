import {
  WebSocketGateway,
  SubscribeMessage,
  WebSocketServer,
} from '@nestjs/websockets';
import { UseGuards } from '@nestjs/common';
import { JwtGuard } from 'src/guard';
import { User } from 'src/decorator';
import { NotificationsService } from './notifications.service';
import { Server, Socket } from 'socket.io';

@WebSocketGateway()
export class NotificationsGateway {
  @WebSocketServer()
  server: Server;

  constructor(private readonly notificationsService: NotificationsService) {}

  @UseGuards(JwtGuard)
  @SubscribeMessage('notifications')
  getNotifications(@User('email') userEmail: string) {
    return this.notificationsService.getNotifications(userEmail);
  }
}
