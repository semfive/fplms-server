package plms.ManagementService.model.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import plms.ManagementService.model.dto.StudentDTO;

@Getter
@Setter
@NoArgsConstructor
public class StudentInClassResponse extends StudentDTO {
    Integer groupNumber;
    Integer vote;
}
