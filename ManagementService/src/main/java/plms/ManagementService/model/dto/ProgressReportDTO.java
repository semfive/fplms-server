package plms.ManagementService.model.dto;

import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProgressReportDTO {
	private Integer id;
	private String title;
	private String content;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Ho_Chi_Minh")
	private Date reportTime;
	private Integer studentId;
	private Integer groupId;
	
	@Override
	public String toString() {
		return "ProgressReportDTO [id=" + id + ", groupId=" + groupId + "]";
	}
	
}
