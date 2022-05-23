package plms.ManagementService.exception;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import plms.ManagementService.controller.response.Response;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public Response<String> handleUnwantedException(Exception e) {
        logger.error(e.getMessage());
        return new Response<>(500, "Unknown error");
    }

    @ExceptionHandler(CustomException.class)
    public Response<String> handleCustomException(CustomException e) {
        logger.warn("{}: {}", e.getCode(), e.getMessage());
        return new Response<>(e.getCode(), e.getMessage());
    }
}
