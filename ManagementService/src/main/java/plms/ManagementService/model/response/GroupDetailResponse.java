package plms.ManagementService.model.response;

import java.sql.Timestamp;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import plms.ManagementService.model.dto.ProjectDTO;
import plms.ManagementService.model.dto.StudentDTO;
@Getter
@Setter
@NoArgsConstructor
public class GroupDetailResponse {
	private Integer id;
    private Integer number;
    private Integer memberQuantity;
    private Integer currentNumber;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "Asia/Ho_Chi_Minh")
    private Timestamp enrollTime;
    private ProjectDTO projectDTO;
    private Integer leaderId;
    private Set<StudentDTO> studentDtoSet;
}
