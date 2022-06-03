package plms.ManagementService.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

	@Override
	public String toString() {
		return "ClassDTO [id=" + id + ", name=" + name + "]";
	}
    
    
}
