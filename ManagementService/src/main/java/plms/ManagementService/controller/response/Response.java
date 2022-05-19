package plms.ManagementService.controller.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Response<E> {
    private Integer code;
    private String message;
    private E data;

    public Response() {
    }

    public Response(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Response(Integer code, String message, E data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
