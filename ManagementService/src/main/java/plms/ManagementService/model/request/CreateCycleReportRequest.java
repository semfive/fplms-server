package plms.ManagementService.model.request;

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
public class CreateCycleReportRequest {
	private String content;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "Asia/Ho_Chi_Minh")
	private Timestamp reportTime;
	private String resourceLink;
	
	@Override
	public String toString() {
		return "CreateCycleReportRequest [content=" + content + ", reportTime=" + reportTime + ", resourceLink="
				+ resourceLink + "]";
	}
	
	
}
