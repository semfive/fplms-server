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
    Integer groupNumber;
    Integer vote;
}
