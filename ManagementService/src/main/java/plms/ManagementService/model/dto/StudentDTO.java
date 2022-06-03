package plms.ManagementService.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentDTO {
    private Integer id;
    private String name;
    private String email;
    private String code;
    
	@Override
	public String toString() {
		return "StudentDTO [id=" + id + ", name=" + name + "]";
	}
    
}
