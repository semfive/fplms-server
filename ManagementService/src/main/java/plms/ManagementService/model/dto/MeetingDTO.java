package plms.ManagementService.model.dto;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MeetingDTO {
	private Integer id;
	private String title;
	private String link;
	private String feedback;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "Asia/Ho_Chi_Minh")
	private Timestamp scheduleTime;
	private Integer lecturerId;
	private Integer groupId;

}
