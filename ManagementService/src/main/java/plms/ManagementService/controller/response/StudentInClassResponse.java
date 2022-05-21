package plms.ManagementService.controller.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import plms.ManagementService.dto.GroupDTO;
import plms.ManagementService.dto.StudentDTO;

@Getter
@Setter
@NoArgsConstructor
public class StudentInClassResponse extends StudentDTO {
    public StudentInClassResponse(Integer id, String name, String email, String code, Integer groupNumber) {
        super(id, name, email, code);
        this.groupNumber = groupNumber;
    }
    Integer groupNumber;
}
