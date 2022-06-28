package plms.ManagementService.model.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NotificationDTO {
    private String tittle;
    private String url;
    private String userEmail;
}
