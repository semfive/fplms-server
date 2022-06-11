import { IsOptional, IsString, IsUrl, Length } from 'class-validator';

export class CreateNotificationDto {
  @IsString()
  @Length(10, 50)
  title: string;

  @IsString()
  @IsUrl()
  @IsOptional()
  @Length(10, 50)
  url: string;
}
