import { ExecutionContext } from '@nestjs/common';
import { AuthGuard } from '@nestjs/passport';

export class JwtGuard extends AuthGuard('jwt') {
  constructor() {
    super();
  }
  // getRequest(context: ExecutionContext) {
  //   const ws = context.switchToWs().getData();
  //   const request = context
  //   return {
  //     headers: {
  //       authorization: ws
  //         .getArgByIndex(0)
  //         .handshake.headers.authorization.split(' ')[1],
  //     },
  //   };
  // }
}
