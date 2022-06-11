import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';

async function bootstrap() {
  const app = await NestFactory.create(AppModule);
  app.setGlobalPrefix('api/notification');
  app.enableCors();
  await app.listen(process.env.SERVER_PORT);
}
bootstrap();
