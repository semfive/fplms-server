package plms.ManagementService.controller;

import java.sql.Timestamp;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonFormat;

import plms.ManagementService.model.dto.CycleReportDTO;
import plms.ManagementService.model.dto.ProgressReportDTO;
import plms.ManagementService.model.request.CreateCycleReportRequest;
import plms.ManagementService.model.request.CreateProgressReportRequest;
import plms.ManagementService.model.response.Response;
import plms.ManagementService.service.ReportService;

@RestController
@RequestMapping("/api/management/classes/{classId}/groups/{groupId}")
public class ReportController {
	@Autowired
	ReportService reportService;
	
	@GetMapping("/cycle-reports")
	public Response<Set<CycleReportDTO>> getCycleReportFromGroup(@PathVariable Integer classId,
			@PathVariable Integer groupId,
			@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "Asia/Ho_Chi_Minh")
			@RequestParam(required = false, name = "startDate") Timestamp startDate,
			@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "Asia/Ho_Chi_Minh")
			@RequestParam(required = false, name = "endDate") Timestamp endDate) {
		return reportService.getCycleReportInGroup(classId, groupId, startDate, endDate);
	}
	
	@PostMapping("/cycle-reports")
	public Response<Void> addCycleReport(@PathVariable Integer groupId,
			@RequestBody CreateCycleReportRequest createCycleReportRequest) {
		return reportService.addCycleReport(createCycleReportRequest, groupId, 1);
	}
	
	@DeleteMapping("/cycle-reports/{reportId}")
	public Response<Void> deleteCycleReport(@PathVariable Integer groupId,
				@PathVariable Integer reportId) {
		return reportService.deleteCycleReport(groupId, reportId, 1);
	}
	
	@GetMapping("/progress-reports")
	public Response<Set<ProgressReportDTO>> getProgressReportFromGroup(@PathVariable Integer classId,
			@PathVariable Integer groupId,
			@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "Asia/Ho_Chi_Minh")
			@RequestParam(required = false, name = "startDate") Timestamp startDate,
			@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "Asia/Ho_Chi_Minh")
			@RequestParam(required = false, name = "endDate") Timestamp endDate) {
		return reportService.getProgressReportInGroup(classId, groupId, startDate, endDate);
	}
	
	@PostMapping("/progress-reports")
	public Response<Void> addProgressReport(@PathVariable Integer groupId,
								@RequestBody CreateProgressReportRequest createProgressReportRequest) {
		return reportService.addProgressReport(createProgressReportRequest, groupId, 5);
	}
	
	@DeleteMapping("/progress-reports/{reportId}")
	public Response<Void> deleteProgressReport(@PathVariable Integer groupId,
				@PathVariable Integer reportId) {
		return reportService.deleteProgressReport(groupId, reportId, 1);
	}

}
