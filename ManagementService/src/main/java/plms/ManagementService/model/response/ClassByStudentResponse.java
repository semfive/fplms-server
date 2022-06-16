package plms.ManagementService.model.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import plms.ManagementService.model.dto.LecturerDTO;

@Getter
@Setter
@NoArgsConstructor
public class ClassByStudentResponse {
    private Integer id;
    private String name;
    private String semesterCode;
    private String enrollKey;
    private Integer subjectId;
    private LecturerDTO lecturerDto;
    private boolean isJoin;
}
