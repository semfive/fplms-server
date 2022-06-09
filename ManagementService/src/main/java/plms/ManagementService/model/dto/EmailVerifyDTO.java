package plms.ManagementService.model.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EmailVerifyDTO {
    String email;
    String role;
}
