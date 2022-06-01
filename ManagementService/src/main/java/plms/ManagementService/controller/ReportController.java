package plms.ManagementService.controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import plms.ManagementService.model.dto.CycleReportDTO;
import plms.ManagementService.model.dto.ProgressReportDTO;
import plms.ManagementService.model.response.Response;
import plms.ManagementService.service.ReportService;

@RestController
@RequestMapping("/api/management/classes/{classId}/groups/{groupId}")
public class ReportController {
	@Autowired
	ReportService reportService;
	
	@GetMapping("/cycle-reports")
	public Response<Set<CycleReportDTO>> getCycleReportFromGroup(@PathVariable Integer classId,
											@PathVariable Integer groupId) {
		return reportService.getCycleReportInGroup(classId, groupId);
	}
	
	@PostMapping("/cycle-reports")
	public Response<Void> addCycleReport(@PathVariable Integer groupId,
			@RequestBody CycleReportDTO cycleReportDto) {
		return reportService.addCycleReport(cycleReportDto, groupId, 1);
	}
	
	@DeleteMapping("/cycle-reports/{reportId}")
	public Response<Void> deleteCycleReport(@PathVariable Integer groupId,
				@PathVariable Integer reportId) {
		return reportService.deleteCycleReport(groupId, reportId, 1);
	}
	
	@GetMapping("/progress-reports")
	public Response<Set<ProgressReportDTO>> getProgressReportFromGroup(@PathVariable Integer classId,
											@PathVariable Integer groupId) {
		return reportService.getProgressReportInGroup(classId, groupId);
	}
	
	@PostMapping("/progress-reports")
	public Response<Void> addProgressReport(@PathVariable Integer groupId,
								@RequestBody ProgressReportDTO progressReportDto) {
		return reportService.addProgressReport(progressReportDto, groupId, 5);
	}
	
	@DeleteMapping("/progress-reports/{reportId}")
	public Response<Void> deleteProgressReport(@PathVariable Integer groupId,
				@PathVariable Integer reportId) {
		return reportService.deleteProgressReport(groupId, reportId, 1);
	}

}
