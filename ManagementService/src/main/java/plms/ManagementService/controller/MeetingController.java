package plms.ManagementService.controller;

import java.sql.Timestamp;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonFormat;

import plms.ManagementService.config.interceptor.GatewayConstant;
import plms.ManagementService.model.dto.MeetingDTO;
import plms.ManagementService.model.response.Response;
import plms.ManagementService.service.AuthenticationService;
import plms.ManagementService.service.MeetingService;

@RestController
@RequestMapping("/api/management/meetings")
public class MeetingController {
	@Autowired
	MeetingService meetingService;
	@Autowired
	AuthenticationService authenticationService;
	@GetMapping("/classes/{classId}/groups/{groupId}")
	public Response<Set<MeetingDTO>> getMeetingInGroup(@PathVariable Integer classId, 
			@PathVariable Integer groupId, @RequestAttribute(required = false) String userRole,
			@RequestAttribute(required = false) String userEmail,
			@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "Asia/Ho_Chi_Minh")
			@RequestParam(required = false, name = "startDate") Timestamp startDate,
			@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "Asia/Ho_Chi_Minh")
			@RequestParam(required = false, name = "endDate") Timestamp endDate) {
		if (userRole.equals(GatewayConstant.ROLE_LECTURE)) {
			return meetingService.getMeetingInGroupByLecturer(classId, groupId, startDate, endDate, userEmail);
		} 
		if (userRole.equals(GatewayConstant.ROLE_STUDENT)) {
			return meetingService.getMeetingInGroupByStudent(classId, groupId, startDate, endDate, userEmail);
		}
		return new Response<>(403, "Not have role access");
	}
	@PostMapping
	public Response<Void> scheduleMeetingByLecturer(@RequestBody MeetingDTO meetingDTO,@RequestAttribute(required = false) String userEmail){
		meetingDTO.setLecturerId(authenticationService.getLectureIdByEmail(userEmail));
		return meetingService.scheduleMeetingByLecturer(meetingDTO);
	}
}
