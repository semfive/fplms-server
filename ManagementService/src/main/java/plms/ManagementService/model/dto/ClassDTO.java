package plms.ManagementService.model.dto;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ClassDTO {
    private Integer id;
    private String name;
    private String semester;
    private String enrollKey;
    private Integer subjectId;

    public ClassDTO(String name, String semester, String enrollKey, Integer subjectId) {
        this.name = name;
        this.semester = semester;
        this.enrollKey = enrollKey;
        this.subjectId = subjectId;
    }

    
    
}
