package plms.ManagementService.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class ClassDTO {
    private Integer id;
    private String name;
    private String semester;
    private String enrollKey;
    private Integer subjectId;
}
