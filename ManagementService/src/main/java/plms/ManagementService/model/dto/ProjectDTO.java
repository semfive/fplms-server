package plms.ManagementService.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDTO {
    private Integer id;
    private String theme;
    private String name;
    private String problem;
    private String context;
    private String actors;
    private String requirements;
    private Integer subjectId;
    
	@Override
	public String toString() {
		return "ProjectDTO [id=" + id + ", name=" + name + "]";
	}
    
}
