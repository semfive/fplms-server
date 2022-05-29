package plms.ManagementService.model.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {
    private Boolean isLecturer;
    private String name;
    private String email;
    private String code;
    private String imageUrl;

    public CreateUserRequest(Boolean isLecturer, String name, String email, String imageUrl) {
        this.isLecturer = isLecturer;
        this.name = name;
        this.email = email;
        this.imageUrl = imageUrl;
    }
}
