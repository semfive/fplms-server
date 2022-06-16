package plms.ManagementService.model.request;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CreateProgressReportRequest {
	private String title;
	private String content;
	private Integer groupId;
	
	@Override
	public String toString() {
		return "CreateProgressReportRequest [title=" + title + "]";
	}
	
}
