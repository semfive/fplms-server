import {
  IsNotEmpty,
  IsOptional,
  IsString,
  IsUrl,
  Length,
} from 'class-validator';

export class GetNotificationsDto {
  @IsString()
  @Length(10, 50)
  @IsNotEmpty()
  title: string;

  @IsString()
  @Length(10, 50)
  @IsOptional()
  @IsUrl()
  url?: string;
}
