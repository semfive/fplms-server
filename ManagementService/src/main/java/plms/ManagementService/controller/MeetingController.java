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
	
	@GetMapping
	public Response<Set<MeetingDTO>> getMeetingInGroup(@RequestParam(required = false) Integer classId, 
			@RequestParam(required = false) Integer groupId, @RequestAttribute(required = false) String userRole,
			@RequestAttribute(required = false) String userEmail,
			@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "Asia/Ho_Chi_Minh")
			@RequestParam(required = false, name = "startDate") Timestamp startDate,
			@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "Asia/Ho_Chi_Minh")
			@RequestParam(required = false, name = "endDate") Timestamp endDate) {
		if (userRole.equals(GatewayConstant.ROLE_LECTURER)) {
			return meetingService.getMeetingInGroupByLecturer(classId, groupId, startDate, endDate, userEmail);
		} 
		if (userRole.equals(GatewayConstant.ROLE_STUDENT)) {
			return meetingService.getMeetingInGroupByStudent(classId, groupId, startDate, endDate, userEmail);
		}
		return new Response<>(403, "Not have role access");
	}
	
	@GetMapping("/{meetingId}")
	public Response<MeetingDTO> getMeetingById(@PathVariable Integer meetingId,
			@RequestAttribute(required = false) String userRole,
			@RequestAttribute(required = false) String userEmail) {
		if (userRole.equals(GatewayConstant.ROLE_LECTURER)) {
			return meetingService.getMeetingDetailByLecturer(userEmail, meetingId);
		} 
		if (userRole.equals(GatewayConstant.ROLE_STUDENT)) {
			return meetingService.getMeetingDetailByStudent(userEmail, meetingId);
		}
		return new Response<>(403, "Not have role access");
	}
	
	
	@PostMapping
	public Response<Void> scheduleMeetingByLecturer(@RequestBody MeetingDTO meetingDTO,@RequestAttribute(required = false) String userEmail){
		meetingDTO.setLecturerId(authenticationService.getLectureIdByEmail(userEmail));
		return meetingService.scheduleMeetingByLecturer(meetingDTO);
	}
	@PutMapping
	public Response<Void> updateMeetingByLecturer(@RequestBody MeetingDTO meetingDTO,@RequestAttribute(required = false) String userEmail){
		meetingDTO.setLecturerId(authenticationService.getLectureIdByEmail(userEmail));
		return meetingService.updateMeetingByLecturer(meetingDTO);
	}

	@DeleteMapping
	public Response<Void> deleteMeetingByLecturer(@RequestParam Integer meetingId,@RequestAttribute(required = false) String userEmail){
		return meetingService.deleteMeetingByLecturer(authenticationService.getLectureIdByEmail(userEmail),meetingId);
	}
}
