import { Global, Injectable } from '@nestjs/common';
import { ConfigModule } from '@nestjs/config';

@Injectable()
export class ConfigService extends ConfigModule {
  constructor() {
    super();
  }
}
