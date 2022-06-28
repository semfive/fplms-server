import { IncomingMessage, ServerResponse } from "http";
import { CLIENT_ABORTED } from "./constants";
type Dependencies = {
  req: IncomingMessage;
  res: ServerResponse;
  errorMessage: string | null;
};
async function logger({ req, res, errorMessage }: Dependencies) {
  const { rawHeaders, httpVersion, method, socket, url } = req;
  const { remoteAddress, remoteFamily } = socket;
  const { statusCode, statusMessage } = res;
  const headers = res.getHeaders();

  if (errorMessage === "") {
    console.log(
      JSON.stringify({
        timestamp: Date.now(),
        //   processingTime: Date.now() - requestStart,
        rawHeaders,
        // data,
        errorMessage,
        httpVersion,
        method,
        remoteAddress,
        remoteFamily,
        url,
        response: {
          statusCode,
          statusMessage,
          headers,
        },
      })
    );
  } else if (errorMessage === CLIENT_ABORTED) {
    console.log(
      JSON.stringify({
        timestamp: Date.now(),
        //   processingTime: Date.now() - requestStart,
        rawHeaders,
        // data,
        errorMessage,
        httpVersion,
        method,
        remoteAddress,
        remoteFamily,
        url,
        response: {
          statusCode,
          statusMessage,
          headers,
        },
      })
    );
  } else {
    console.log(
      JSON.stringify({
        timestamp: Date.now(),
        //   processingTime: Date.now() - requestStart,
        rawHeaders,
        // data,
        errorMessage,
        httpVersion,
        method,
        remoteAddress,
        remoteFamily,
        url,
        response: {
          statusCode,
          statusMessage,
          headers,
        },
      })
    );
  }
}

// const removeHandlers = (req: IncomingMessage, res: ServerResponse) => {
//   req.off("data", getChunk);
//   req.off("end", assembleBody);
//   req.off("error", getError);
//   res.off("close", logClose);
//   res.off("error", logError);
//   res.off("finish", logFinish);
// };

export { logger };
