package plms.ManagementService.model.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GroupDTO {
    private Integer id;
    private Integer number;
    private Integer memberQuantity;
    private boolean isJoin;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "Asia/Ho_Chi_Minh")
    private Timestamp enrollTime;
    private boolean isDisable;
    private ProjectDTO projectDTO;
    
}
