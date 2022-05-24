package plms.ManagementService.model.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ClassByStudentResponse {
	private Integer id;
    private String name;
    private String semester;
    private String enrollKey;
    private Integer subjectId;
    private boolean isJoin;
}
