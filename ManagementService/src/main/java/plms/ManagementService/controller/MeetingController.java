package plms.ManagementService.controller;

import java.sql.Timestamp;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonFormat;

import plms.ManagementService.model.dto.MeetingDTO;
import plms.ManagementService.model.response.Response;
import plms.ManagementService.service.AuthenticationService;
import plms.ManagementService.service.MeetingService;

@RestController
@RequestMapping("/api/management/classes/{classId}/groups/{groupId}/meetings")
public class MeetingController {
	@Autowired
	MeetingService meetingService;
	@Autowired
	AuthenticationService authenticationService;
	@GetMapping
	public Response<Set<MeetingDTO>> getMeetingInGroup(@PathVariable Integer classId, 
			@PathVariable Integer groupId,
			@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "Asia/Ho_Chi_Minh")
			@RequestParam(required = false, name = "startDate") Timestamp startDate,
			@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "Asia/Ho_Chi_Minh")
			@RequestParam(required = false, name = "endDate") Timestamp endDate) {
		return meetingService.getMeetingInGroup(classId, groupId, startDate, endDate);
	}
	@PostMapping
	public Response<Void> scheduleMeetingByLecturer(@RequestBody MeetingDTO meetingDTO,@RequestAttribute(required = false) String userEmail){
		meetingDTO.setLecturerId(authenticationService.getLectureIdByEmail(userEmail));
		return meetingService.scheduleMeetingByLecturer(meetingDTO);
	}
}
