package plms.ManagementService.model.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupDTO {
    private Integer id;
    private Integer number;
    private Boolean isEmpty;
}
