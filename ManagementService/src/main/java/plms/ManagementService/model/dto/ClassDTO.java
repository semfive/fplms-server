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
    private String semesterCode;
    private String enrollKey;
    private Integer subjectId;

    public ClassDTO(String name, String semesterCode, String enrollKey, Integer subjectId) {
        this.name = name;
        this.semesterCode = semesterCode;
        this.enrollKey = enrollKey;
        this.subjectId = subjectId;
    }

    
    
}
