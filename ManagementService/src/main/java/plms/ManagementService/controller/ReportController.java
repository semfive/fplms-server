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
import plms.ManagementService.model.response.Response;
import plms.ManagementService.service.ReportService;

@RestController
@RequestMapping("/api/management/classes/{classId}/groups/{groupId}/cycle-reports")
public class ReportController {
	@Autowired
	ReportService reportService;
	
	@GetMapping
	public Response<Set<CycleReportDTO>> getCycleReportFromGroup(@PathVariable Integer classId,
											@PathVariable Integer groupId) {
		return reportService.getCycleReportInGroup(classId, groupId);
	}
	
	@PostMapping
	public Response<Void> addCycleReport(@PathVariable Integer groupId,
			@RequestBody CycleReportDTO cycleReportDto) {
		return reportService.addCycleReport(cycleReportDto, groupId, 1);
	}
	
	@DeleteMapping("/{reportId}")
	public Response<Void> deleteCycleReport(@PathVariable Integer groupId,
				@PathVariable Integer reportId) {
		return reportService.deleteCycleReport(groupId, reportId, 1);
	}

}
