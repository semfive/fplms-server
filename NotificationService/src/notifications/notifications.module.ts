import { HttpModule } from '@nestjs/axios';
import { Module } from '@nestjs/common';
import { JwtStrategy } from 'src/strategy';
import { NotificationsController } from './notifications.controller';
import { NotificationsService } from './notifications.service';
import { NotificationsGateway } from './notifications.gateway';

@Module({
  imports: [HttpModule, JwtStrategy],
  controllers: [NotificationsController],
  providers: [NotificationsService, NotificationsGateway],
})
export class NotificationsModule {}
