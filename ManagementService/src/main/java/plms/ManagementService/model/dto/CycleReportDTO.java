package plms.ManagementService.model.dto;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CycleReportDTO {
	private Integer id;
	private String title;
	private String content;
	private Integer cycleNumber;
	private String feedback;
	private String resourceLink;
	private Float mark;
	private Integer groupId;
	
	@Override
	public String toString() {
		return "CycleReportDTO [id=" + id + ", groupId=" + groupId + "]";
	}
	
}
