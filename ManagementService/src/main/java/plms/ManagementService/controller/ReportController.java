package plms.ManagementService.controller;

import java.sql.Timestamp;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonFormat;

import plms.ManagementService.config.interceptor.GatewayConstant;
import plms.ManagementService.model.dto.CycleReportDTO;
import plms.ManagementService.model.dto.ProgressReportDTO;
import plms.ManagementService.model.request.CreateCycleReportRequest;
import plms.ManagementService.model.request.CreateProgressReportRequest;
import plms.ManagementService.model.response.Response;
import plms.ManagementService.service.ReportService;
import plms.ManagementService.service.StudentService;

@RestController
@RequestMapping("/api/management/classes/{classId}/groups/{groupId}")
public class ReportController {
	@Autowired
	ReportService reportService;
	@Autowired
    StudentService studentService;
	
	@GetMapping("/cycle-reports")
	public Response<Set<CycleReportDTO>> getCycleReportFromGroup(@PathVariable Integer classId,
			@PathVariable Integer groupId, @RequestAttribute(required = false) String userRole,
			@RequestAttribute(required = false) String userEmail,
			@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "Asia/Ho_Chi_Minh")
			@RequestParam(required = false, name = "startDate") Timestamp startDate,
			@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "Asia/Ho_Chi_Minh")
			@RequestParam(required = false, name = "endDate") Timestamp endDate) {
		if (userRole.equals(GatewayConstant.ROLE_LECTURE)) {
			return reportService.getCycleReportInGroupByLecturer(classId, groupId, startDate, endDate, userEmail);
		} 
		if (userRole.equals(GatewayConstant.ROLE_STUDENT)) {
			return reportService.getCycleReportInGroupByStudent(classId, groupId, startDate, endDate, userEmail);
		}
		return new Response<>(403, "Not have role access");
		
	}
	
	@PostMapping("/cycle-reports")
	public Response<Void> addCycleReport(@RequestAttribute(required = false) String userEmail,
			@PathVariable Integer groupId,
			@RequestBody CreateCycleReportRequest createCycleReportRequest) {
		Integer studentId = studentService.getStudentIdByEmail(userEmail);
		return reportService.addCycleReport(createCycleReportRequest, groupId, studentId);
	}
	
	@DeleteMapping("/cycle-reports/{reportId}")
	public Response<Void> deleteCycleReport(@RequestAttribute(required = false) String userEmail,
			@PathVariable Integer groupId, @PathVariable Integer reportId) {
		Integer studentId = studentService.getStudentIdByEmail(userEmail);
		return reportService.deleteCycleReport(groupId, reportId, studentId);
	}
	
	@GetMapping("/progress-reports")
	public Response<Set<ProgressReportDTO>> getProgressReportFromGroup(@PathVariable Integer classId,
			@PathVariable Integer groupId, @RequestAttribute(required = false) String userRole,
			@RequestAttribute(required = false) String userEmail,
			@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "Asia/Ho_Chi_Minh")
			@RequestParam(required = false, name = "startDate") Timestamp startDate,
			@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "Asia/Ho_Chi_Minh")
			@RequestParam(required = false, name = "endDate") Timestamp endDate) {
		if (userRole.equals(GatewayConstant.ROLE_LECTURE)) {
			return reportService.getProgressReportInGroupByLecturer(classId, groupId, startDate, endDate, userEmail);
		} 
		if (userRole.equals(GatewayConstant.ROLE_STUDENT)) {
			return reportService.getProgressReportInGroupByStudent(classId, groupId, startDate, endDate, userEmail);
		}
		return new Response<>(403, "Not have role access");
	}
	
	@PostMapping("/progress-reports")
	public Response<Void> addProgressReport(@RequestAttribute(required = false) String userEmail,
			@PathVariable Integer groupId,
			@RequestBody CreateProgressReportRequest createProgressReportRequest) {
		Integer studentId = studentService.getStudentIdByEmail(userEmail);
		return reportService.addProgressReport(createProgressReportRequest, groupId, studentId);
	}
	
	@DeleteMapping("/progress-reports/{reportId}")
	public Response<Void> deleteProgressReport(@RequestAttribute(required = false) String userEmail,
			@PathVariable Integer groupId, @PathVariable Integer reportId) {
		Integer studentId = studentService.getStudentIdByEmail(userEmail);
		return reportService.deleteProgressReport(groupId, reportId, studentId);
	}

}
