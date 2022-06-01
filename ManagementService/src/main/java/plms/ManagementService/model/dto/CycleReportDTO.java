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
	private String content;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "Asia/Ho_Chi_Minh")
	private Timestamp reportTime;
	private String feedback;
	private String resourceLink;
	private Integer groupId;
	
	public CycleReportDTO(String content, Timestamp reportTime, String resourceLink, Integer groupId) {
		this.content = content;
		this.reportTime = reportTime;
		this.resourceLink = resourceLink;
		this.groupId = groupId;
	}
	
}
