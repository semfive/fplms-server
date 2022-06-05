package plms.ManagementService.model.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCycleReportRequest {
	private String title;
	private String content;
	private String resourceLink;
	
	@Override
	public String toString() {
		return "CreateCycleReportRequest [title=" + title + "]";
	}
	
}
